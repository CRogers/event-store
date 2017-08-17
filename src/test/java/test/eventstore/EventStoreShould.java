package test.eventstore;

import org.junit.Before;
import org.junit.Test;
import uk.callumr.eventstore.EventStore;
import uk.callumr.eventstore.core.Event;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EventStoreShould {
    private static final String JAMES = "james";
    private static final String EVENT_DATA = "eventData";
    private static final String OTHER_EVENT_DATA = "other eventData";

    private final EventStore eventStore;

    public EventStoreShould(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Before
    public void before() {
        eventStore.clear();
    }

    @Test
    public void return_an_event_given_one_was_inserted_for_a_given_entity_id() {
        String entityId = "entity";

        eventStore.addEvent(entityId, EVENT_DATA);
        List<Event> events = eventStore.eventsFor(entityId);

        assertSingleEventWithIdData(events, entityId, EVENT_DATA);
    }

    @Test
    public void return_only_one_event_even_though_another_was_inserted_for_another_entity_id() {
        eventStore.addEvent(JAMES, EVENT_DATA);
        eventStore.addEvent("alex", "other data");

        List<Event> events = eventStore.eventsFor(JAMES);

        assertSingleEventWithIdData(events, JAMES, EVENT_DATA);
    }

    @Test
    public void return_two_events_in_insertion_order_when_inserted_for_the_same_entity() {
        eventStore.addEvent(JAMES, EVENT_DATA);
        eventStore.addEvent(JAMES, OTHER_EVENT_DATA);

        List<Event> events = eventStore.eventsFor(JAMES);


    }

    private void assertSingleEventWithIdData(List<Event> events, String entityId, String eventData) {
        assertThat(events).hasSize(1);
        Event event = events.get(0);
        assertThat(event.entityId()).isEqualTo(entityId);
        assertThat(event.eventData()).isEqualTo(eventData);
    }

}
