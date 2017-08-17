package uk.callumr.eventstore;

import uk.callumr.eventstore.core.EntityId;
import uk.callumr.eventstore.core.Event;

import java.util.List;

public interface EventStore {
    void addEvent(EntityId entityId, String eventData);

    List<Event> eventsFor(EntityId entityId);

    void clear();
}
