package example;

import uk.callumr.eventstore.EventStore;
import uk.callumr.eventstore.core.EntityId;
import uk.callumr.eventstore.core.EventType;

import java.util.stream.Stream;

public class MealMachine {
    private static final EventType RECIPE_BOOK_CREATED = EventType.of("recipe_book_created");

    private final EventStore eventStore;

    public MealMachine(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    public RecipeBook addRecipeBook(String recipeBookName) {
        EntityId recipeBookId = EntityId.random();

        eventStore.addEvent(recipeBookId, RECIPE_BOOK_CREATED.newEvent(recipeBookName));

        return RecipeBook.builder()
                .id(recipeBookId)
                .name(recipeBookName)
                .build();
    }

    public Stream<RecipeBook> allRecipeBooks() {
        return eventStore.eventsOfType(RECIPE_BOOK_CREATED).stream()
                .map(event -> RecipeBook.builder()
                        .name(event.data())
                        .id(event.entityId())
                        .build());
    }
}
