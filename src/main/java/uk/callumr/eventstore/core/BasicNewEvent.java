package uk.callumr.eventstore.core;

import org.immutables.value.Value;

@Value.Immutable
public interface BasicNewEvent extends NewEvent {
    static ImmutableBasicNewEvent.Builder builder() {
        return ImmutableBasicNewEvent.builder();
    }
}
