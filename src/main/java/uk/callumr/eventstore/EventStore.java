package uk.callumr.eventstore;

import uk.callumr.eventstore.core.*;

import java.util.List;

public interface EventStore {
    void addEvent(Event event);

    List<VersionedEvent> events(EventFilters filters);

    void clear();
}
