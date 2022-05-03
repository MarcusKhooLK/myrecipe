package edu.nus.iss.sg.myrecipe.services;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import edu.nus.iss.sg.myrecipe.models.Recipe;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonReader;

@Service
public class SearchService {

    private static final String URL_SEARCH_NAME = "https://www.themealdb.com/api/json/v1/1/search.php";
    private static final String URL_SEARCH_ID = "https://www.themealdb.com/api/json/v1/1/lookup.php";

    public List<Recipe> searchRecipes(String searchString, Boolean convertAll) {
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
            Recipe r = Recipe.convert(jRecipes.getJsonObject(i), convertAll);
            recipes.add(r);
        }

        return recipes;
    }

    public Optional<Recipe> searchRecipeById(String recipedId) {

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
        JsonArray jRecipes = reader.readObject().getJsonArray("meals");

        if (jRecipes == null)
            return Optional.empty();

        Recipe recipe = Recipe.convert(jRecipes.getJsonObject(0), true);

        return Optional.of(recipe);
    }
}
