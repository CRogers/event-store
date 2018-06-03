package example;

import uk.callumr.eventstore.EventStore;
import uk.callumr.eventstore.core.EntityId;

public class MealMachine {
    private final EventStore eventStore;

    public MealMachine(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    public RecipeBook addRecipeBook(String recipeBookName) {
        EntityId recipeBookId = EntityId.random();

//        eventStore.addEvent(recipeBookId, );

        return RecipeBook.builder()
                .id(recipeBookId)
                .build();
    }
}
