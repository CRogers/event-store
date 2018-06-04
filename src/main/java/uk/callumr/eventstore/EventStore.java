package uk.callumr.eventstore;

import uk.callumr.eventstore.core.*;

import java.util.stream.Stream;

public interface EventStore {
    void addEvent(Event event);

    Stream<VersionedEvent> events(EventFilters filters);

    void clear();
}
