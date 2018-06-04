package example;

import org.immutables.value.Value;
import uk.callumr.eventstore.core.EntityId;

import java.util.UUID;
import java.util.stream.Stream;

@Value.Immutable
public abstract class Recipe {
    public abstract EntityId id();
    public abstract String name();

    public Meal cook() {
        return Meal.builder()
                .id(EntityId.of(UUID.randomUUID().toString()))
                .build();
    }

    public Stream<Meal> allMeals() {
        return Stream.of();
    }

    public static ImmutableRecipe.Builder builder() {
        return ImmutableRecipe.builder();
    }
}
