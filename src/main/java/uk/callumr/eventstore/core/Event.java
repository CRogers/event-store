package uk.callumr.eventstore.core;

public interface Event {
    EventType eventType();
    String data();
}
