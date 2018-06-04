package uk.callumr.eventstore.core;

import org.immutables.value.Value;

@Value.Immutable
public interface VersionedEvent {
    long version();
    EntityId entityId();
    EventType eventType();
    String data();

    static ImmutableVersionedEvent.Builder builder() {
        return ImmutableVersionedEvent.builder();
    }
}
