package uk.callumr.eventstore;

import uk.callumr.eventstore.core.EntityId;
import uk.callumr.eventstore.core.Event;
import uk.callumr.eventstore.core.EventType;
import uk.callumr.eventstore.core.NewEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class InMemoryEventStore implements EventStore {
    private AtomicLong version;
    private List<Event> events;

    public InMemoryEventStore() {
        clear();
    }

    @Override
    public void clear() {
        version = new AtomicLong(0);
        events = new ArrayList<>();
    }

    @Override
    public void addEvent(EntityId entityId, NewEvent newEvent) {
        events.add(Event.builder()
                .version(version.getAndIncrement())
                .entityId(entityId)
                .eventType(newEvent.eventType())
                .data(newEvent.data())
                .build()
        );
    }

    @Override
    public List<Event> eventsFor(EntityId entityId) {
        return events.stream()
                .filter(event -> event.entityId().equals(entityId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> eventsOfType(EventType eventType) {
        return events.stream()
                .filter(event -> event.eventType().equals(eventType))
                .collect(Collectors.toList());
    }
}
