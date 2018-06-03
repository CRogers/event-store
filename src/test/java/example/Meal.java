package example;

import org.immutables.value.Value;
import uk.callumr.eventstore.core.EntityId;

@Value.Immutable
public abstract class Meal {
    protected abstract EntityId id();

    public static ImmutableMeal.Builder builder() {
        return ImmutableMeal.builder();
    }
}
