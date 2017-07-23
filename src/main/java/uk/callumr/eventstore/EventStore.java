package uk.callumr.eventstore;

import java.util.List;

public interface EventStore {
    List<Object> allEvents();
}
