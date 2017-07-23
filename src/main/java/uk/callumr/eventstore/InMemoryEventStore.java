package uk.callumr.eventstore;

import uk.callumr.eventstore.core.Event;

import java.util.Collections;
import java.util.List;

public class InMemoryEventStore implements EventStore {

    @Override
    public List<Event> allEvents() {
        return Collections.emptyList();
    }
}
