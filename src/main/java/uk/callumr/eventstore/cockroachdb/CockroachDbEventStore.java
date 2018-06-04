package uk.callumr.eventstore.cockroachdb;

import org.jooq.*;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultDataType;
import org.jooq.impl.SQLDataType;
import uk.callumr.eventstore.EventStore;
import uk.callumr.eventstore.core.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Function;
import java.util.stream.Stream;

public class CockroachDbEventStore implements EventStore {
    private static final DataType<Long> SERIAL = new DefaultDataType<>(SQLDialect.POSTGRES, Long.class, "serial").nullable(false);
    private static final Field<Long> VERSION = DSL.field("version", SERIAL);
    private static final Field<String> ENTITY_ID = DSL.field("entityId", SQLDataType.VARCHAR.nullable(false));
    private static final Field<String> EVENT_TYPE = DSL.field("eventType", SQLDataType.VARCHAR.nullable(false));
    private static final Field<String> DATA = DSL.field("data", SQLDataType.VARCHAR.nullable(false));
    private static final Table<Record> EVENTS = DSL.table("hi.events");

    private final DSLContext jooq;

    static {
        System.getProperties().setProperty("org.jooq.no-logo", "true");
    }

    public CockroachDbEventStore(String jdbcUrl) {
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
        deleteAll();
        createDatabase();
        createEventsTable();
    }

    private void deleteAll() {
        jooq.transaction(configuration -> DSL.using(configuration).query("drop database hi").execute());
    }

    private void createDatabase() {
        jooq.transaction(configuration -> DSL.using(configuration).query("create database hi").execute());
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
        jooq.transaction(configuration -> DSL.using(configuration)
                .insertInto(EVENTS)
                .columns(ENTITY_ID, EVENT_TYPE, DATA)
                .values(event.entityId().asString(), event.eventType().asString(), event.data())
                .execute());
    }

    @Override
    public Stream<VersionedEvent> events(EventFilters filters) {
        EventFilter eventFilter = filters.stream()
                .findFirst()
                .get();

        Condition condition = EventFilter.caseOf(eventFilter)
                .forEntity(entityId -> ENTITY_ID.equal(entityId.asString()))
                .ofType(eventType -> EVENT_TYPE.equal(eventType.asString()));

        return jooq.transactionResult(configuration -> {
            SelectConditionStep<Record4<Long, String, String, String>> query = DSL.using(configuration)
                    .select(VERSION, ENTITY_ID, EVENT_TYPE, DATA)
                    .from(EVENTS)
                    .where(condition);

            System.out.println(query.getSQL());

            return query
                    .stream()
                    .map(this::toVersionedEvent);
        });
    }

    @Override
    public void reproject(EventFilters filters, Function<Stream<VersionedEvent>, Stream<Event>> projectionFunc) {
        projectionFunc.apply(events(filters))
                .forEach(this::addEvent);
    }

    private VersionedEvent toVersionedEvent(Record4<Long, String, String, String> record) {
        return VersionedEvent.builder()
                .version(record.component1())
                .event(BasicEvent.builder()
                        .entityId(EntityId.of(record.component2()))
                        .eventType(EventType.of(record.component3()))
                        .data(record.component4())
                        .build())
                .build();
    }
}
