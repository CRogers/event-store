package uk.callumr.eventstore;

import java.util.Collections;
import java.util.List;

public class InMemoryEventStore implements EventStore {

    @Override
    public List<Object> allEvents() {
        return Collections.emptyList();
    }
}
