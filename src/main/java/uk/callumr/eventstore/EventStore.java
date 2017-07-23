package uk.callumr.eventstore;

import uk.callumr.eventstore.core.Event;

import java.util.List;

public interface EventStore {
    List<Event> allEvents();
}
