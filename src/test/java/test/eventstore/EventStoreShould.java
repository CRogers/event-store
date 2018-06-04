package test.eventstore;

import org.junit.Before;
import org.junit.Test;
import uk.callumr.eventstore.EventStore;
import uk.callumr.eventstore.core.EntityId;
import uk.callumr.eventstore.core.Event;
import uk.callumr.eventstore.core.EventType;
import uk.callumr.eventstore.core.VersionedEvent;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.callumr.eventstore.core.EventFilters.forEntity;
import static uk.callumr.eventstore.core.EventFilters.ofType;

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

        Stream<VersionedEvent> events = eventStore.events(forEntity(JAMES));

        assertThatSteamContainsEvents(events, event);
    }

    @Test
    public void return_two_events_in_insertion_order_when_inserted_for_the_same_entity() {
        Event jamesEvent1 = EVENT_TYPE.newEvent(JAMES, EVENT_DATA);
        Event jamesEvent2 = EVENT_TYPE.newEvent(JAMES, OTHER_EVENT_DATA);

        eventStore.addEvent(jamesEvent1);
        eventStore.addEvent(jamesEvent2);

        Stream<VersionedEvent> events = eventStore.events(forEntity(JAMES));

        assertThatSteamContainsEvents(events,
                jamesEvent1,
                jamesEvent2);
    }

    @Test
    public void get_events_with_filter_for_just_entity_id() {
        Event jamesEvent = Event.of(JAMES, EVENT_TYPE, EVENT_DATA);
        Event alexEvent = Event.of(ALEX, EVENT_TYPE, EVENT_DATA);

        eventStore.addEvent(jamesEvent);
        eventStore.addEvent(alexEvent);

        Stream<VersionedEvent> events = eventStore.events(forEntity(JAMES));

        assertThatSteamContainsEvents(events, jamesEvent);
    }

    @Test
    public void get_events_with_filter_for_just_event_type() {
        Event someEvent = Event.of(JAMES, EVENT_TYPE, EVENT_DATA);
        Event otherEvent = Event.of(JAMES, OTHER_EVENT_TYPE, EVENT_DATA);

        eventStore.addEvent(someEvent);
        eventStore.addEvent(otherEvent);

        Stream<VersionedEvent> events = eventStore.events(ofType(EVENT_TYPE));



        assertThatSteamContainsEvents(events, someEvent);
    }

    private static void assertThatSteamContainsEvents(Stream<VersionedEvent> eventStream, Event... expectedEvents) {
        assertThat(eventStream.map(VersionedEvent::event)).containsExactly(expectedEvents);
    }

}
