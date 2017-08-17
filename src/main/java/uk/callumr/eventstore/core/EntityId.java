package uk.callumr.eventstore.core;

import org.immutables.value.Value;

@Value.Immutable
public abstract class EntityId {
    public abstract String asString();

    public static EntityId of(String id) {
        return ImmutableEntityId.builder()
                .asString(id)
                .build();
    }
}
