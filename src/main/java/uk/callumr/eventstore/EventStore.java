package uk.callumr.eventstore;

import uk.callumr.eventstore.core.EntityId;
import uk.callumr.eventstore.core.Event;
import uk.callumr.eventstore.core.VersionedEvent;
import uk.callumr.eventstore.core.EventType;

import java.util.List;

public interface EventStore {
    void addEvent(EntityId entityId, Event event);

    List<VersionedEvent> eventsFor(EntityId entityId);

    List<VersionedEvent> eventsOfType(EventType eventType);

    void clear();
}
