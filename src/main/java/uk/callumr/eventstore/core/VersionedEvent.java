package uk.callumr.eventstore.core;

import org.immutables.value.Value;

@Value.Immutable
public interface VersionedEvent {
    long version();
    Event event();

    default EntityId entityId() {
        return event().entityId();
    }

    default EventType eventType() {
        return event().eventType();
    }

    default String data() {
        return event().data();
    }

    static ImmutableVersionedEvent.Builder builder() {
        return ImmutableVersionedEvent.builder();
    }
}
