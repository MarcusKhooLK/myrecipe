package edu.nus.iss.sg.myrecipe.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import edu.nus.iss.sg.myrecipe.models.Recipe;

@Repository
public class RecipeRepository {
    
    @Autowired
    private JdbcTemplate template;

    public Boolean insertRecipe(Recipe recipe, Integer user_id) {
        if(recipe.getIngredients().size() > 10)
            return false;

        if(recipe.getMeasurements().size() > 10) {
            return false;
        }

        int result = template.update(SQL.CREATE_RECIPE, 
            recipe.getName(),
            recipe.getCategory(),
            recipe.getCountry(),
            recipe.getInstructions(),
            recipe.getThumbnail(),
            recipe.getYoutubeLink(),
            recipe.getIngredients().get(0),
            recipe.getIngredients().get(1),
            recipe.getIngredients().get(2),
            recipe.getIngredients().get(3),
            recipe.getIngredients().get(4),
            recipe.getIngredients().get(5),
            recipe.getIngredients().get(6),
            recipe.getIngredients().get(7),
            recipe.getIngredients().get(8),
            recipe.getIngredients().get(9),
            recipe.getMeasurements().get(0),
            recipe.getMeasurements().get(1),
            recipe.getMeasurements().get(2),
            recipe.getMeasurements().get(3),
            recipe.getMeasurements().get(4),
            recipe.getMeasurements().get(5),
            recipe.getMeasurements().get(6),
            recipe.getMeasurements().get(7),
            recipe.getMeasurements().get(8),
            recipe.getMeasurements().get(9),
            user_id);

        return result == 1;
    }
}
