package uk.callumr.eventstore.core;

import org.immutables.value.Value;

@Value.Immutable
public interface Event {
    long version();
    EntityId entityId();
    EventType eventType();
    String data();

    static ImmutableEvent.Builder builder() {
        return ImmutableEvent.builder();
    }
}
