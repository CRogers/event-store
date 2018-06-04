package uk.callumr.eventstore.core;

import org.immutables.value.Value;

@Value.Immutable
public interface BasicEvent extends Event {
    static ImmutableBasicEvent.Builder builder() {
        return ImmutableBasicEvent.builder();
    }
}
