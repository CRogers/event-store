package uk.callumr.eventstore.core;

import org.immutables.value.Value;

@Value.Immutable
public interface Event {
    long version();
    String eventData();
    EntityId entityId();

    static ImmutableEvent.Builder builder() {
        return ImmutableEvent.builder();
    }
}
