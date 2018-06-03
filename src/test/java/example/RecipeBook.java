package example;

import org.immutables.value.Value;
import uk.callumr.eventstore.core.EntityId;

@Value.Immutable
public abstract class RecipeBook {
    protected abstract EntityId id();
    public abstract String name();

    public static ImmutableRecipeBook.Builder builder() {
        return ImmutableRecipeBook.builder();
    }

}
