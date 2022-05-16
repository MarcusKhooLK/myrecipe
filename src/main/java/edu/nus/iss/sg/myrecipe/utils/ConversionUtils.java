package edu.nus.iss.sg.myrecipe.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.util.MultiValueMap;

import edu.nus.iss.sg.myrecipe.models.Recipe;
import jakarta.json.JsonObject;

public class ConversionUtils {

    public static Recipe convert(MultiValueMap<String, String> form) {
        Recipe recipe = new Recipe();

        String recipeName = form.getFirst("recipeName").trim();
        String recipeCategory = form.getFirst("recipeCategory").trim();
        String recipeArea = form.getFirst("recipeArea").trim();
        String recipeInstructions = form.getFirst("recipeInstructions").trim();
        String recipeYoutubeLink = form.getFirst("recipeYoutubeLink").trim();

        List<String> recipeIngredients = new ArrayList<String>();
        List<String> recipeMeasuremets = new ArrayList<String>();
        int i = 0;
        while (true) {
            String recipeIngredient = form.getFirst("recipeIngredient%d".formatted(i));
            if ((null == recipeIngredient) || (0 == recipeIngredient.trim().length()))
                break;
            String recipeMeasurement = form.getFirst("recipeMeasurement%d".formatted(i));
            recipeIngredients.add(recipeIngredient);
            recipeMeasuremets.add(recipeMeasurement);
            i++;
        }

        recipe.setName(recipeName);
        recipe.setCategory(recipeCategory.isBlank() ? null : recipeCategory);
        recipe.setCountry(recipeArea.isBlank() ? null : recipeArea);
        recipe.setYoutubeLink(recipeYoutubeLink.isBlank() ? null : recipeYoutubeLink);
        recipe.setInstructions(recipeInstructions);
        recipe.setIngredients(recipeIngredients);
        recipe.setMeasurements(recipeMeasuremets);

        return recipe;
    }

    public static Recipe convert(SqlRowSet result) {
        Recipe r = new Recipe();
        r.setRecipeId(String.valueOf(result.getInt("recipe_id")));
        r.setName(result.getString("name"));
        r.setCategory(result.getString("category"));
        r.setCountry(result.getString("country"));
        r.setThumbnail(result.getString("thumbnail"));
        String youtubeLink = result.getString("youtubeLink");
        if(youtubeLink != null) {
            youtubeLink = youtubeLink.replace("/watch?v=", "/embed/");
        }
        r.setYoutubeLink(youtubeLink);
        r.setInstructions(result.getString("instructions"));
        return r;
    }

    public static Recipe convert(JsonObject jsonObject, Boolean convertAll) {
        Recipe recipe = new Recipe();
        recipe.setRecipeId(jsonObject.getString("idMeal", ""));
        recipe.setName(jsonObject.getString("strMeal", ""));
        recipe.setThumbnail(jsonObject.getString("strMealThumb", ""));
        
        // early exit if don't need to convert all
        if(!convertAll) {
            return recipe;
        }

        recipe.setCategory(jsonObject.getString("strCategory", ""));
        recipe.setCountry(jsonObject.getString("strArea", ""));
        recipe.setInstructions(jsonObject.getString("strInstructions", ""));
        String youtubeLink = jsonObject.getString("strYoutube", "");
        youtubeLink = youtubeLink.replace("/watch?v=", "/embed/");
        recipe.setYoutubeLink(youtubeLink);
        recipe.setCreatedBy(null);
        List<String> ingredients = new ArrayList<String>();
        List<String> measurements = new ArrayList<String>();
        for(int i = 1; i <= 20; i++) {
            String temp = jsonObject.getString("strIngredient%d".formatted(i), "");
            if(!temp.isEmpty())
                ingredients.add(temp);
            
            temp = jsonObject.getString("strMeasure%d".formatted(i), "");
            if(!temp.isEmpty())
                measurements.add(temp);
        }
        recipe.setIngredients(ingredients);
        recipe.setMeasurements(measurements);
        return recipe;
    }

    public static String buildUrlEncodedFormEntity(String... params) {
        if ((params.length % 2) > 0) {
            throw new IllegalArgumentException("Need to give an even number of parameters");
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < params.length; i += 2) {
            if (i > 0) {
                result.append('&');
            }
            try {
                result.append(URLEncoder.encode(params[i], StandardCharsets.UTF_8.name())).append('=')
                        .append(URLEncoder.encode(params[i + 1], StandardCharsets.UTF_8.name()));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return result.toString();
    }
    
}
