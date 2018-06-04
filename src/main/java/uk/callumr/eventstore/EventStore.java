package uk.callumr.eventstore;

import uk.callumr.eventstore.core.*;

import java.util.List;

public interface EventStore {
    void addEvent(Event event);

    List<VersionedEvent> eventsFor(EntityId entityId);

    List<VersionedEvent> eventsOfType(EventType eventType);

    void clear();

    List<VersionedEvent> eventsFor(EventFilters filters);
}
