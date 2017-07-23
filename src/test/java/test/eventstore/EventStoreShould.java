package test.eventstore;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import uk.callumr.eventstore.cockroachdb.CockroachDbEventStore;
import uk.callumr.eventstore.EventStore;
import uk.callumr.eventstore.InMemoryEventStore;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class EventStoreShould {
    private final EventStore eventStore;

    public EventStoreShould(String name, EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Parameters(name = "{0}")
    public static Collection<Object[]> parameters() {
        return ImmutableList.of(
                new Object[] { "In Memory", new InMemoryEventStore() },
                new Object[] { "Cockroachdb", new CockroachDbEventStore() }
        );
    }

    @Test
    public void have_no_events_initially() {
        assertThat(eventStore.allEvents()).isEmpty();
    }

    @Test
    public void return_an_event_given_one_was_inserted() {

    }
}
