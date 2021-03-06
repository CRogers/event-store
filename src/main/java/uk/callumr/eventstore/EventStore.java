package uk.callumr.eventstore;

import uk.callumr.eventstore.core.Event;
import uk.callumr.eventstore.core.EventFilters;
import uk.callumr.eventstore.core.VersionedEvent;

import java.util.function.Function;
import java.util.stream.Stream;

public interface EventStore {
    void addEvent(Event event);

    Stream<VersionedEvent> events(EventFilters filters);

    void reproject(EventFilters filters, Function<Stream<VersionedEvent>, Stream<Event>> projectionFunc);

    void clear();
}
