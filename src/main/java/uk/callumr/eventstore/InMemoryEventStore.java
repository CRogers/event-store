package uk.callumr.eventstore;

import uk.callumr.eventstore.core.Event;

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
    public void addEvent(String entityId, String eventData) {
        events.add(Event.builder()
                .version(version.getAndIncrement())
                .entityId(entityId)
                .eventData(eventData)
                .build()
        );
    }

    @Override
    public List<Event> eventsFor(String entityId) {
        return events.stream()
                .filter(event -> event.entityId().equals(entityId))
                .collect(Collectors.toList());
    }
}
