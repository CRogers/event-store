package example;

import org.immutables.value.Value;
import uk.callumr.eventstore.EventStore;
import uk.callumr.eventstore.core.EntityId;
import uk.callumr.eventstore.core.VersionedEvent;
import uk.callumr.eventstore.core.EventType;

import java.util.stream.Stream;

@Value.Immutable
public abstract class RecipeBook {
    private static final EventType RECIPE_CREATED = EventType.of("recipe_created");
    private static final EventType RECIPE_ADDED_TO_RECIPE_BOOK = EventType.of("recipe_added_to_recipe_book");

    @Value.Auxiliary
    protected abstract EventStore eventStore();

    public abstract EntityId id();
    public abstract String name();

    public static ImmutableRecipeBook.Builder builder() {
        return ImmutableRecipeBook.builder();
    }

    public Recipe addRecipe(String recipeName) {
        EntityId recipeId = EntityId.random();

        eventStore().addEvent(RECIPE_CREATED.newEvent(recipeId, recipeName));
        eventStore().addEvent(RECIPE_ADDED_TO_RECIPE_BOOK.newEvent(id(), recipeId.asString()));

        return Recipe.builder()
                .id(recipeId)
                .name(recipeName)
                .build();
    }

    public Stream<Recipe> allRecipes() {
        return eventStore().eventsOfType(RECIPE_ADDED_TO_RECIPE_BOOK).stream()
                .map(event -> {
                    EntityId recipeId = EntityId.of(event.data());
                    VersionedEvent recipeCreatedEvent = eventStore().events(recipeId).stream()
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("never happen"));
                    return Recipe.builder()
                            .id(recipeId)
                            .name(recipeCreatedEvent.data())
                            .build();
                });
    }
}
