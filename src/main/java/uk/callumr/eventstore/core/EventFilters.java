package uk.callumr.eventstore.core;

import org.immutables.value.Value;

import java.util.Set;

@Value.Immutable
public abstract class EventFilters {
    protected abstract Set<EventFilter> filters();

    public static class Builder extends ImmutableEventFilters.Builder {
        public Builder allEventForEntity(EntityId entityId) {
            addFilters(EventFilterImpls.allEventForEntity(entityId));
            return this;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
