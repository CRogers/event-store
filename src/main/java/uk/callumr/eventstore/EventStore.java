package uk.callumr.eventstore;

import uk.callumr.eventstore.core.*;

import java.util.List;

public interface EventStore {
    void addEvent(Event event);

    default List<VersionedEvent> events(EntityId entityId) {
        return events(EventFilters.forEntity(entityId));
    }

    default List<VersionedEvent> eventsOfType(EventType eventType) {
        return events(EventFilters.ofType(eventType));
    }

    void clear();

    List<VersionedEvent> events(EventFilters filters);
}
