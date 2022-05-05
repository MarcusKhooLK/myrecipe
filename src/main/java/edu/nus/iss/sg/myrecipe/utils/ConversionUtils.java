package edu.nus.iss.sg.myrecipe.utils;

import java.util.Arrays;

import org.springframework.util.MultiValueMap;

import edu.nus.iss.sg.myrecipe.models.Recipe;

public class ConversionUtils {

    public static Recipe convert(MultiValueMap<String, String> form) {
        Recipe recipe = new Recipe();

        String recipeName = form.getFirst("recipeName");
        String recipeCategory = form.getFirst("recipeCategory");
        String recipeCountry = form.getFirst("recipeCountry");
        String recipeInstructions = form.getFirst("recipeInstructions");
        String recipeThumbnail = form.getFirst("recipeThumbnail");
        String recipeYoutubeLink = form.getFirst("recipeYoutubeLink");

        String[] recipeIngredients = new String[10];
        Arrays.fill(recipeIngredients, null);
        String[] recipeMeasuremets = new String[10];
        Arrays.fill(recipeMeasuremets, null);
        int i = 0;
        while (true) {
            String recipeIngredient = form.getFirst("recipeIngredient%d".formatted(i));
            if ((null == recipeIngredient) || (0 == recipeIngredient.trim().length()))
                break;
            String recipeMeasurement = form.getFirst("recipeMeasurement%d".formatted(i));
            recipeIngredients[i] = recipeIngredient;
            recipeMeasuremets[i] = recipeMeasurement;
            i++;
        }

        recipe.setName(recipeName);
        recipe.setCategory(recipeCategory);
        recipe.setCountry(recipeCountry);
        recipe.setThumbnail(recipeThumbnail);
        recipe.setYoutubeLink(recipeYoutubeLink);
        recipe.setInstructions(recipeInstructions);
        recipe.setIngredients(Arrays.asList(recipeIngredients));
        recipe.setMeasurements(Arrays.asList(recipeMeasuremets));

        return recipe;
    }
    
}
