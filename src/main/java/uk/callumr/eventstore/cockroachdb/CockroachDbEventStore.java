package uk.callumr.eventstore.cockroachdb;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import uk.callumr.eventstore.EventStore;
import uk.callumr.eventstore.core.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CockroachDbEventStore implements EventStore {
    private final CockroachEvents cockroachEvents;

    public CockroachDbEventStore() {
        DBI dbi = new DBI("jdbc:postgresql://localhost:26257/hi", "root", "root");
        dbi.registerMapper(new EventMapper());
        this.cockroachEvents = dbi.onDemand(CockroachEvents.class);
        try {
            this.cockroachEvents.deleteAll();
        } catch (UnableToExecuteStatementException e) {
            // ignore
        }
        this.cockroachEvents.createDatabase();
        this.cockroachEvents.createEventsTable();
    }

    @Override
    public void addEvent(String entityId, String eventData) {
        cockroachEvents.insertIt(entityId, eventData);
    }

    @Override
    public List<Event> eventsFor(String entityId) {
        return cockroachEvents.allEvents(entityId);
    }

    public interface CockroachEvents {
        @SqlUpdate("insert into hi.events (entityId, data) values (:entityId, :data)")
        void insertIt(@Bind("entityId") String entityId, @Bind("data") String data);

        @SqlQuery("select * from hi.events where entityId = :entityId")
        List<Event> allEvents(@Bind("entityId") String entityId);

        @SqlUpdate("drop database hi")
        void deleteAll();

        @SqlUpdate("create database hi")
        void createDatabase();

        @SqlUpdate("create table hi.events (version serial primary key, entityId string, data string)")
        void createEventsTable();
    }

    public class EventMapper implements ResultSetMapper<Event> {
        @Override
        public Event map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            long version = r.getLong("version");
            String entityId = r.getString("entityId");
            String eventData = r.getString("data");

            return Event.builder()
                    .version(version)
                    .entityId(entityId)
                    .eventData(eventData)
                    .build();
        }
    }
}
