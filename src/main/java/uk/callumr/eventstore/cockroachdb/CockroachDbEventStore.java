package uk.callumr.eventstore.cockroachdb;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.StatementContext;
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
    @Override
    public List<Event> allEvents() {
        DBI dbi = new DBI("jdbc:postgresql://localhost:26257/hi", "root", "root");
        dbi.registerMapper(new EventMapper());
        CoachroachEvents coachroachEvents = dbi.onDemand(CoachroachEvents.class);
        coachroachEvents.insertIt("entity", "blagh");
        return coachroachEvents.allEvents();
    }

    public interface CoachroachEvents {
        @SqlUpdate("insert into hi.events (entityId, data) values (:entityId, :data)")
        void insertIt(@Bind("entityId") String entityId, @Bind("data") String data);

        @SqlQuery("select * from hi.events")
        List<Event> allEvents();
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
