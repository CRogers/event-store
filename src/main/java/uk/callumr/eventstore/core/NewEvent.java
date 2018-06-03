package uk.callumr.eventstore.core;

import org.immutables.value.Value;

@Value.Immutable
public interface NewEvent {
    EventType eventType();
    String data();

    static ImmutableNewEvent.Builder builder() {
        return ImmutableNewEvent.builder();
    }
}
