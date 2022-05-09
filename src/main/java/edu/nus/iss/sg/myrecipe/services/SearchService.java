package edu.nus.iss.sg.myrecipe.services;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import edu.nus.iss.sg.myrecipe.models.Recipe;
import edu.nus.iss.sg.myrecipe.utils.ConversionUtils;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonReader;

@Service
public class SearchService {

    @Autowired
    private RecipeService recipeSvc;

    private static final String URL_SEARCH_NAME = "https://www.themealdb.com/api/json/v1/1/search.php";
    private static final String URL_SEARCH_ID = "https://www.themealdb.com/api/json/v1/1/lookup.php";

    public List<Recipe> searchRecipesFromMealDb(String searchString) {
        final String searchUrl = UriComponentsBuilder.fromUriString(URL_SEARCH_NAME)
                .queryParam("s", searchString)
                .toUriString();

        RestTemplate restTemplate = new RestTemplate();
        RequestEntity<Void> req = RequestEntity.get(searchUrl)
                .accept(MediaType.APPLICATION_JSON)
                .build();
        ResponseEntity<String> resp = restTemplate.exchange(req, String.class);
        final String payload = resp.getBody();
        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonArray jRecipes = null;
        List<Recipe> recipes = new ArrayList<Recipe>();
        
        try{
            jRecipes = reader.readObject().getJsonArray("meals");
        } catch(ClassCastException ex) {
            return recipes;
        }
        
        if(jRecipes == null)
            return recipes;

        for (int i = 0; i < jRecipes.size(); i++) {
            Recipe r = ConversionUtils.convert(jRecipes.getJsonObject(i), false);
            recipes.add(r);
        }

        return recipes;
    }

    public List<Recipe> searchRecipesFromMyRecipeDb(String searchString) {
        List<Recipe> recipes = recipeSvc.getUserRecipesByName(searchString);

        // add a dummy author so that hyperlink can use a different endpoint
        recipes.forEach(r -> {
            r.setCreatedBy("u");
        });

        return recipes;
    }

    public Optional<Recipe> searchRecipeFromMealDbById(Integer recipedId) {

        final String searchUrl = UriComponentsBuilder.fromUriString(URL_SEARCH_ID)
                .queryParam("i", recipedId)
                .toUriString();
        RestTemplate restTemplate = new RestTemplate();
        RequestEntity<Void> req = RequestEntity.get(searchUrl)
                .accept(MediaType.APPLICATION_JSON)
                .build();

        ResponseEntity<String> resp = restTemplate.exchange(req, String.class);
        final String payload = resp.getBody();
        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonArray jRecipes = null;
        try{
            jRecipes = reader.readObject().getJsonArray("meals");
        } catch(ClassCastException ex) {
            return Optional.empty();
        }

        if (jRecipes == null)
            return Optional.empty();

        Recipe recipe = ConversionUtils.convert(jRecipes.getJsonObject(0), true);

        return Optional.of(recipe);
    }

    public Optional<Recipe> searchRecipeFromMyRecipeDbById(Integer recipeId) {
        Optional<Recipe> recipe = recipeSvc.getRecipeByRecipeId(recipeId);
        return recipe;
    }
}
