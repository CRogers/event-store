package uk.callumr.eventstore.cockroachdb;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import uk.callumr.eventstore.EventStore;

import java.util.List;

public class CockroachDbEventStore implements EventStore {
    @Override
    public List<Object> allEvents() {
        DBI dbi = new DBI("jdbc:postgresql://localhost:26257/hi", "root", "root");
        CoachroachEvents coachroachEvents = dbi.onDemand(CoachroachEvents.class);
        coachroachEvents.insertIt();
        return null;
    }

    public interface CoachroachEvents {
        @SqlUpdate("insert into hi.test (value) values ('lol')")
        public void insertIt();
    }
}
