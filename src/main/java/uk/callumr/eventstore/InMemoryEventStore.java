package uk.callumr.eventstore;

import uk.callumr.eventstore.core.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryEventStore implements EventStore {
    private final AtomicLong version = new AtomicLong(0);
    private final List<Event> events = new ArrayList<>();

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
        return Collections.unmodifiableList(events);
    }
}
