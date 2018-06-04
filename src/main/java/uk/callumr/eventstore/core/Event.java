package uk.callumr.eventstore.core;

public interface Event {
    EntityId entityId();
    EventType eventType();
    String data();
}
