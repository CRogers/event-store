package uk.callumr.eventstore;

import uk.callumr.eventstore.core.EntityId;
import uk.callumr.eventstore.core.VersionedEvent;
import uk.callumr.eventstore.core.EventType;
import uk.callumr.eventstore.core.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class InMemoryEventStore implements EventStore {
    private AtomicLong version;
    private List<VersionedEvent> events;

    public InMemoryEventStore() {
        clear();
    }

    @Override
    public void clear() {
        version = new AtomicLong(0);
        events = new ArrayList<>();
    }

    @Override
    public void addEvent(Event event) {
        events.add(VersionedEvent.builder()
                .version(version.getAndIncrement())
                .entityId(event.entityId())
                .eventType(event.eventType())
                .data(event.data())
                .build()
        );
    }

    @Override
    public List<VersionedEvent> eventsFor(EntityId entityId) {
        return events.stream()
                .filter(event -> event.entityId().equals(entityId))
                .collect(Collectors.toList());
    }

    @Override
    public List<VersionedEvent> eventsOfType(EventType eventType) {
        return events.stream()
                .filter(event -> event.eventType().equals(eventType))
                .collect(Collectors.toList());
    }
}
