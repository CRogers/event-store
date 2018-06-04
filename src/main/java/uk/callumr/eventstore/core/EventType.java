package uk.callumr.eventstore.core;

import org.immutables.value.Value;

@Value.Immutable
public abstract class EventType {
    public abstract String asString();

    public static EventType of(String eventType) {
        return ImmutableEventType.builder()
                .asString(eventType)
                .build();
    }

    public Event newEvent(String data) {
        return BasicEvent.builder()
                .eventType(this)
                .data(data)
                .build();
    }
}
