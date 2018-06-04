package uk.callumr.eventstore;

import uk.callumr.eventstore.core.EntityId;
import uk.callumr.eventstore.core.VersionedEvent;
import uk.callumr.eventstore.core.EventType;
import uk.callumr.eventstore.core.NewEvent;

import java.util.List;

public interface EventStore {
    void addEvent(EntityId entityId, NewEvent newEvent);

    List<VersionedEvent> eventsFor(EntityId entityId);

    List<VersionedEvent> eventsOfType(EventType eventType);

    void clear();
}
