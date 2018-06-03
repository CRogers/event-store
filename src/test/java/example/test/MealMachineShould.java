package example.test;

import example.Meal;
import example.MealMachine;
import example.Recipe;
import example.RecipeBook;
import org.junit.Test;
import uk.callumr.eventstore.InMemoryEventStore;

import static org.assertj.core.api.Assertions.assertThat;

public class MealMachineShould {
    private final MealMachine mealMachine = new MealMachine(new InMemoryEventStore());

    @Test
    public void allow_a_recipe_book_and_recipe_to_be_added_and_used() {
        RecipeBook recipeBook = mealMachine.addRecipeBook("ye olde recipe book");
        Recipe recipe = recipeBook.addRecipe("tasty pancakes");
        Meal meal = recipe.cook();

        assertThat(recipe.allMeals()).containsExactly(meal);
    }
}