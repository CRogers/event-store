package uk.callumr.eventstore;

import uk.callumr.eventstore.core.*;

import java.util.List;

public interface EventStore {
    void addEvent(Event event);

    void clear();

    List<VersionedEvent> events(EventFilters filters);
}
