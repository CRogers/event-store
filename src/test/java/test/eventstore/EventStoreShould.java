package test.eventstore;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import uk.callumr.eventstore.EventStore;
import uk.callumr.eventstore.InMemoryEventStore;
import uk.callumr.eventstore.cockroachdb.CockroachDbEventStore;
import uk.callumr.eventstore.core.Event;

import java.util.Collection;
import java.util.List;

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

    @Before
    public void before() {
        eventStore.clear();
    }

    @Test
    public void return_an_event_given_one_was_inserted_for_a_given_entity_id() {
        String entityId = "entity";
        String eventData = "eventData";

        eventStore.addEvent(entityId, eventData);
        List<Event> events = eventStore.eventsFor(entityId);

        assertSingleEventWithIdData(events, entityId, eventData);
    }

    @Test
    public void return_only_one_event_even_though_another_was_inserted_for_another_entity_id() {
        eventStore.addEvent("james", "data");
        eventStore.addEvent("alex", "other data");

        List<Event> events = eventStore.eventsFor("james");

        assertSingleEventWithIdData(events, "james", "data");
    }

    private void assertSingleEventWithIdData(List<Event> events, String entityId, String eventData) {
        assertThat(events).hasSize(1);
        Event event = events.get(0);
        assertThat(event.entityId()).isEqualTo(entityId);
        assertThat(event.eventData()).isEqualTo(eventData);
    }
}
