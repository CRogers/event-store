package uk.callumr.eventstore.cockroachdb;

import org.jooq.*;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultDataType;
import org.jooq.impl.SQLDataType;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import uk.callumr.eventstore.EventStore;
import uk.callumr.eventstore.core.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class CockroachDbEventStore implements EventStore {
    private static final DataType<Long> SERIAL = new DefaultDataType<>(SQLDialect.POSTGRES, Long.class, "serial").nullable(false);
    private static final Field<Long> VERSION = DSL.field("version", SERIAL);
    private static final Field<String> ENTITY_ID = DSL.field("entityId", SQLDataType.VARCHAR.nullable(false));
    private static final Field<String> EVENT_TYPE = DSL.field("eventType", SQLDataType.VARCHAR.nullable(false));
    private static final Field<String> DATA = DSL.field("data", SQLDataType.VARCHAR.nullable(false));
    private static final Table<Record> EVENTS = DSL.table("hi.events");

    private final DBI dbi;
    private final CockroachEvents cockroachEvents;
    private final DSLContext jooq;

    static {
        System.getProperties().setProperty("org.jooq.no-logo", "true");
    }

    public CockroachDbEventStore(String jdbcUrl) {
        this.dbi = new DBI(jdbcUrl, "root", "root");
        this.dbi.registerMapper(new EventMapper());
        this.cockroachEvents = this.dbi.onDemand(CockroachEvents.class);

        this.jooq = DSL.using(new ConnectionProvider() {
            @Override
            public Connection acquire() throws DataAccessException {
                try {
                    return DriverManager.getConnection(jdbcUrl, "root", "root");
                } catch (SQLException e) {
                    throw new DataAccessException("could not open connection", e);
                }
            }

            @Override
            public void release(Connection connection) throws DataAccessException {
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new DataAccessException("could not close connection", e);
                }
            }
        }, SQLDialect.POSTGRES);
    }

    @Override
    public void clear() {
        try {
            this.cockroachEvents.deleteAll();
        } catch (UnableToExecuteStatementException e) {
            // ignore
        }
        this.cockroachEvents.createDatabase();
        createEventsTable();
    }

    private void createEventsTable() {
        jooq.transaction(configuration -> DSL.using(configuration)
                .createTable(EVENTS)
                .column(VERSION)
                .column(ENTITY_ID)
                .column(EVENT_TYPE)
                .column(DATA)
                .constraint(DSL.primaryKey(VERSION)).execute());

    }

    @Override
    public void addEvent(Event event) {
        cockroachEvents.insertIt(event.entityId().asString(), event.eventType().asString(), event.data());
    }

    @Override
    public List<VersionedEvent> eventsFor(EntityId entityId) {
        return jooq.transactionResult(configuration -> {
            return DSL.using(configuration)
                    .select(VERSION, ENTITY_ID, EVENT_TYPE, DATA)
                    .from(EVENTS)
                    .where(ENTITY_ID.equal(entityId.asString()))
                    .stream()
                    .map(record -> VersionedEvent.builder()
                            .version(record.component1())
                            .event(BasicEvent.builder()
                                    .entityId(EntityId.of(record.component2()))
                                    .eventType(EventType.of(record.component3()))
                                    .data(record.component4())
                                    .build())
                            .build())
                    .collect(Collectors.toList());
            });
    }

    @Override
    public List<VersionedEvent> eventsFor(EventFilters filters) {
        EventFilter eventFilter = filters.stream()
                .findFirst()
                .get();

        return EventFilter.caseOf(eventFilter)
                .forEntity(this::eventsFor)
                .ofType(this::eventsOfType);
    }

    @Override
    public List<VersionedEvent> eventsOfType(EventType eventType) {
        return cockroachEvents.allEventsOfType(eventType.asString());
    }

    public interface CockroachEvents {
        @SqlUpdate("insert into hi.events (entityId, eventType, data) values (:entityId, :eventType, :data)")
        void insertIt(@Bind("entityId") String entityId, @Bind("eventType") String eventType, @Bind("data") String data);

        @SqlQuery("select * from hi.events where entityId = :entityId")
        List<VersionedEvent> allEvents(@Bind("entityId") String entityId);

        @SqlQuery("select * from hi.events where eventType = :eventType")
        List<VersionedEvent> allEventsOfType(@Bind("eventType") String eventType);

        @SqlUpdate("drop database hi")
        void deleteAll();

        @SqlUpdate("create database hi")
        void createDatabase();

        @SqlUpdate("create table hi.events (version serial primary key, entityId string, eventType string, data string)")
        void createEventsTable();
    }

    public class EventMapper implements ResultSetMapper<VersionedEvent> {
        @Override
        public VersionedEvent map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            long version = r.getLong("version");
            String entityId = r.getString("entityId");
            String eventType = r.getString("eventType");
            String data = r.getString("data");

            return VersionedEvent.builder()
                    .version(version)
                    .event(BasicEvent.builder()
                            .entityId(EntityId.of(entityId))
                            .eventType(EventType.of(eventType))
                            .data(data)
                            .build())
                    .build();
        }
    }
}
