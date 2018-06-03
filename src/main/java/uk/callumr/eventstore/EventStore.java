package uk.callumr.eventstore;

import uk.callumr.eventstore.core.EntityId;
import uk.callumr.eventstore.core.Event;
import uk.callumr.eventstore.core.NewEvent;

import java.util.List;

public interface EventStore {
    void addEvent(EntityId entityId, NewEvent newEvent);

    List<Event> eventsFor(EntityId entityId);

    void clear();
}
