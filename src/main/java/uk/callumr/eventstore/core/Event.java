package uk.callumr.eventstore.core;

import org.immutables.value.Value;

@Value.Immutable
public interface Event {
    long version();
    String entityId();
    String eventData();

    static ImmutableEvent.Builder builder() {
        return ImmutableEvent.builder();
    }
}
