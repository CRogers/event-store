package example;

import org.immutables.value.Value;
import uk.callumr.eventstore.core.EntityId;

@Value.Immutable
public abstract class RecipeBook {
    protected abstract EntityId id();

    public Recipe addRecipe(String recipeName) {
        return Recipe.builder()
                .id(EntityId.of(recipeName))
                .build();
    }

    public static ImmutableRecipeBook.Builder builder() {
        return ImmutableRecipeBook.builder();
    }
}
