package test.eventstore;

import org.junit.Before;
import org.junit.Test;
import uk.callumr.eventstore.EventStore;
import uk.callumr.eventstore.core.EntityId;
import uk.callumr.eventstore.core.Event;
import uk.callumr.eventstore.core.VersionedEvent;
import uk.callumr.eventstore.core.EventType;

import java.util.List;

import static matchers.EventMatcher.matchingEvent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public abstract class EventStoreShould {
    private static final EntityId JAMES = EntityId.of("james");
    private static final EntityId ALEX = EntityId.of("alex");
    private static final EventType EVENT_TYPE = EventType.of("eventType");
    private static final EventType OTHER_EVENT_TYPE = EventType.of("otherEventType");
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
        Event event = EVENT_TYPE.newEvent(JAMES, EVENT_DATA);
        eventStore.addEvent(event);

        List<VersionedEvent> events = eventStore.eventsFor(JAMES);

        assertThat(events, contains(
                matchingEvent(event)
        ));
    }

    @Test
    public void return_only_one_event_even_though_another_was_inserted_for_another_entity_id() {
        Event jamesEvent = EVENT_TYPE.newEvent(JAMES, EVENT_DATA);
        Event alexEvent = EVENT_TYPE.newEvent(ALEX, OTHER_EVENT_DATA);

        eventStore.addEvent(jamesEvent);
        eventStore.addEvent(alexEvent);

        List<VersionedEvent> events = eventStore.eventsFor(JAMES);

        assertThat(events, contains(
                matchingEvent(jamesEvent)
        ));
    }

    @Test
    public void return_two_events_in_insertion_order_when_inserted_for_the_same_entity() {
        Event jamesEvent1 = EVENT_TYPE.newEvent(JAMES, EVENT_DATA);
        Event jamesEvent2 = EVENT_TYPE.newEvent(JAMES, OTHER_EVENT_DATA);

        eventStore.addEvent(jamesEvent1);
        eventStore.addEvent(jamesEvent2);

        List<VersionedEvent> events = eventStore.eventsFor(JAMES);

        assertThat(events, contains(
                matchingEvent(jamesEvent1),
                matchingEvent(jamesEvent2)
        ));
    }

    @Test
    public void return_events_of_a_specific_type() {
        Event event = EVENT_TYPE.newEvent(JAMES, "yo");
        Event otherEvent = OTHER_EVENT_TYPE.newEvent(JAMES, "hi");

        eventStore.addEvent(event);
        eventStore.addEvent(otherEvent);

        List<VersionedEvent> events = eventStore.eventsOfType(EVENT_TYPE);

        assertThat(events, contains(
                matchingEvent(event)
        ));
    }

//    @Test
//    public void update_() {
//        eventStore.projectNewEvents(EventFilters.builder()
//                .allEventForEntity(JAMES)
//                .build(), events -> Stream.of())
//
//        eventStore.projectNewEvents(ImmutableSet.of(JAMES, ALEX), ImmutableSet.of(EVENT_TYPE, OTHER_EVENT_TYPE), events -> {
//           return Stream.of()
//        });
//    }

}
