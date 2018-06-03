package example.test;

import example.MealMachine;
import example.RecipeBook;
import org.junit.Test;
import uk.callumr.eventstore.InMemoryEventStore;

import static org.assertj.core.api.Assertions.assertThat;

public class MealMachineShould {
    private final MealMachine mealMachine = new MealMachine(new InMemoryEventStore());

    @Test
    public void allow_a_recipe_book_to_be_added_and_retrieved() {
        String recipeBookName = "ye olde recipe book";
        RecipeBook recipeBook = mealMachine.addRecipeBook(recipeBookName);

        assertThat(recipeBook.name()).isEqualTo(recipeBookName);
        assertThat(mealMachine.allRecipeBooks()).containsExactly(recipeBook);
    }
}