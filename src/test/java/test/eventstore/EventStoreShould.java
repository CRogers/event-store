package test.eventstore;

import org.junit.Before;
import org.junit.Test;
import uk.callumr.eventstore.EventStore;
import uk.callumr.eventstore.core.EntityId;
import uk.callumr.eventstore.core.Event;
import uk.callumr.eventstore.core.EventType;
import uk.callumr.eventstore.core.NewEvent;

import java.util.List;

import static matchers.EventMatcher.matchingEvent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public abstract class EventStoreShould {
    private static final EntityId JAMES = EntityId.of("james");
    private static final EntityId ALEX = EntityId.of("alex");
    private static final EventType EVENT_TYPE = EventType.of("eventType");
    private static final EventType OTHER_EVENT_TYPE = EventType.of("otherEventType");
    private static final NewEvent EVENT_DATA = EVENT_TYPE.newEvent("eventData");
    private static final NewEvent OTHER_EVENT_DATA = EVENT_TYPE.newEvent("other eventData");

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
        eventStore.addEvent(JAMES, EVENT_DATA);
        List<Event> events = eventStore.eventsFor(JAMES);

        assertThat(events, contains(
                matchingEvent(JAMES, EVENT_DATA)
        ));
    }

    @Test
    public void return_only_one_event_even_though_another_was_inserted_for_another_entity_id() {
        eventStore.addEvent(JAMES, EVENT_DATA);
        eventStore.addEvent(ALEX, OTHER_EVENT_DATA);

        List<Event> events = eventStore.eventsFor(JAMES);

        assertThat(events, contains(
                matchingEvent(JAMES, EVENT_DATA)
        ));
    }

    @Test
    public void return_two_events_in_insertion_order_when_inserted_for_the_same_entity() {
        eventStore.addEvent(JAMES, EVENT_DATA);
        eventStore.addEvent(JAMES, OTHER_EVENT_DATA);

        List<Event> events = eventStore.eventsFor(JAMES);

        assertThat(events, contains(
                matchingEvent(JAMES, EVENT_DATA),
                matchingEvent(JAMES, OTHER_EVENT_DATA)
        ));
    }

    @Test
    public void return_events_of_a_specific_type() {
        eventStore.addEvent(JAMES, EVENT_DATA);
        eventStore.addEvent(ALEX, OTHER_EVENT_TYPE.newEvent("hi"));

        List<Event> events = eventStore.eventsOfType(EVENT_TYPE);

        assertThat(events, contains(
                matchingEvent(JAMES, EVENT_DATA)
        ));
    }

}
