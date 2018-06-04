package uk.callumr.eventstore;

import uk.callumr.eventstore.core.*;

import java.util.List;

public interface EventStore {
    void addEvent(Event event);

    default List<VersionedEvent> eventsFor(EntityId entityId) {
        return eventsFor(EventFilters.builder()
                .forEntity(entityId)
                .build());
    }

    default List<VersionedEvent> eventsOfType(EventType eventType) {
        return eventsFor(EventFilters.builder()
                .ofType(eventType)
                .build());
    }

    void clear();

    List<VersionedEvent> eventsFor(EventFilters filters);
}
