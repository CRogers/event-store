package uk.callumr.eventstore.core;

public interface NewEvent {
    EventType eventType();
    String data();
}
