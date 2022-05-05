package edu.nus.iss.sg.myrecipe.repository;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import edu.nus.iss.sg.myrecipe.models.Recipe;
import edu.nus.iss.sg.myrecipe.utils.ConversionUtils;

@Repository
public class RecipeRepository {
    
    @Autowired
    private JdbcTemplate template;

    public Integer insertRecipe(Recipe recipe, Integer userId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(SQL.INSERT_RECIPE, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1,recipe.getName());
            ps.setString(2,recipe.getCategory());
            ps.setString(3,recipe.getCountry());
            ps.setString(4,recipe.getInstructions());
            ps.setString(5,recipe.getThumbnail());
            ps.setString(6,recipe.getYoutubeLink());
            ps.setInt(7, userId);
            return ps;
        }, keyHolder);
        BigInteger bigInt = (BigInteger)keyHolder.getKey();
        return bigInt.intValue();
    }

    public Boolean insertIngredients(List<String> ingredients, List<String> measurements, Integer recipeId) {
        if(ingredients.size() != measurements.size()) {
            throw new IllegalArgumentException();
        }

        List<Object[]> params = new ArrayList<>();
        for(int i = 0; i < ingredients.size(); i++) {
            Object[] row = new Object[3];
            row[0] = ingredients.get(i);
            row[1] = measurements.get(i);
            row[2] = recipeId;
            params.add(row);
        }

        int[] results = template.batchUpdate(SQL.INSERT_INGREDIENT, params);

        for(int i : results) {
            if(i <= 0) {
                throw new IllegalArgumentException();
            }
        }

        return true;
    }

    public List<Recipe> getAllUserRecipesByUserId(Integer userId) {
        final SqlRowSet result = template.queryForRowSet(SQL.SELECT_ALL_RECIPE_BY_USERID, userId);

        List<Recipe> recipes = new ArrayList<>();

        while(result.next()) {
            Recipe r = ConversionUtils.convert(result);
            recipes.add(r);
        }

        return recipes;
    }

    public List<Recipe> getUserRecipesByName(String recipeName) {
        final SqlRowSet result = template.queryForRowSet(SQL.SELECT_RECIPE_BY_NAME, "%"+recipeName+"%");
        List<Recipe> recipes = new ArrayList<>();
        while(result.next()) {
            Recipe r = ConversionUtils.convert(result);
            recipes.add(r);
        }
        return recipes;
    }

    public Optional<Recipe> getRecipeByRecipeId(Integer recipeId) {
        final SqlRowSet recipeResult = template.queryForRowSet(SQL.SELECT_RECIPE_BY_ID, recipeId);
        
        if(recipeResult.next()) {
            Recipe r = ConversionUtils.convert(recipeResult);

            final SqlRowSet username = template.queryForRowSet(SQL.SELECT_USERNAME_BY_RECIPEID, recipeId);
            if(username.next()) {
                r.setCreatedBy(username.getString("username"));
            }

            final SqlRowSet ingredientResult = template.queryForRowSet(SQL.SELECT_INGREDIENTS_BY_RECIPEID, recipeId);
            List<String> ingredients = new ArrayList<String>();
            List<String> measurements = new ArrayList<String>();
            
            while(ingredientResult.next()) {
                ingredients.add(ingredientResult.getString("name"));
                measurements.add(ingredientResult.getString("measurement"));
            }

            r.setIngredients(ingredients);
            r.setMeasurements(measurements);

            return Optional.of(r);
        } else {
            return Optional.empty();
        }
    }
}
