package example.test;

import example.MealMachine;
import example.Recipe;
import example.RecipeBook;
import org.junit.Test;
import uk.callumr.eventstore.InMemoryEventStore;

import static org.assertj.core.api.Assertions.assertThat;

public class MealMachineShould {
    private final InMemoryEventStore eventStore = new InMemoryEventStore();
    private final MealMachine mealMachine = new MealMachine(eventStore);

    @Test
    public void allow_a_recipe_book_to_be_added_and_retrieved() {
        String recipeBookName = "ye olde recipe book";
        RecipeBook recipeBook = mealMachine.addRecipeBook(recipeBookName);

        assertThat(recipeBook.name()).isEqualTo(recipeBookName);
        assertThat(mealMachine.allRecipeBooks()).containsExactly(recipeBook);
    }

    @Test
    public void add_a_recipe_to_a_recipe_book_then_return_that_recipe_from_the_book() {
        String recipeName = "florentine steak";

        RecipeBook recipeBook = mealMachine.addRecipeBook("book");
        Recipe recipe = recipeBook.addRecipe(recipeName);

        assertThat(recipeBook.allRecipes()).hasOnlyOneElementSatisfying(actualRecipe -> {
            assertThat(actualRecipe.name()).isEqualTo(recipeName);
            assertThat(actualRecipe.id()).isEqualTo(recipe.id());
        });
    }
}