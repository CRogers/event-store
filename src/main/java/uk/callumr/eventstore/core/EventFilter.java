package uk.callumr.eventstore.core;

import org.derive4j.Data;
import org.derive4j.Derive;
import org.derive4j.Visibility;

@Data(value = @Derive(inClass = "EventFilterImpls", withVisibility = Visibility.Package))
public abstract class EventFilter {
    interface Cases<R> {
        R allEventForEntity(EntityId entityId);
    }

    public abstract <R> R match(Cases<R> cases);

    @Override
    public abstract int hashCode();
    @Override
    public abstract boolean equals(Object obj);
    @Override
    public abstract String toString();
}
