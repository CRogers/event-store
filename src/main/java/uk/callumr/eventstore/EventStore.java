package uk.callumr.eventstore;

import uk.callumr.eventstore.core.Event;

import java.util.List;

public interface EventStore {
    void addEvent(String entityId, String eventData);

    List<Event> eventsFor(String entityId);
}
