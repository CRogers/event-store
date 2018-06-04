package uk.callumr.eventstore.cockroachdb;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import uk.callumr.eventstore.EventStore;
import uk.callumr.eventstore.core.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CockroachDbEventStore implements EventStore {
    private final CockroachEvents cockroachEvents;

    static {
        System.getProperties().setProperty("org.jooq.no-logo", "true");
    }

    public CockroachDbEventStore(String jdbcUrl) {
        DBI dbi = new DBI(jdbcUrl, "root", "root");
        dbi.registerMapper(new EventMapper());
        this.cockroachEvents = dbi.onDemand(CockroachEvents.class);
    }

    @Override
    public void clear() {
        try {
            this.cockroachEvents.deleteAll();
        } catch (UnableToExecuteStatementException e) {
            // ignore
        }
        this.cockroachEvents.createDatabase();
        this.cockroachEvents.createEventsTable();
    }

    @Override
    public void addEvent(Event event) {
        cockroachEvents.insertIt(event.entityId().asString(), event.eventType().asString(), event.data());
    }

    @Override
    public List<VersionedEvent> eventsFor(EntityId entityId) {
        return cockroachEvents.allEvents(entityId.asString());
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
