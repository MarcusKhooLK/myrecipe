package edu.nus.iss.sg.myrecipe.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.json.JsonObject;

public class Recipe {

    private String recipeId;
    private String name;
    private String category;
    private String country;
    private String instructions;
    private String thumbnail;
    private String youtubeLink;
    private List<String> ingredients = new ArrayList<String>();
    private List<String> measurements = new ArrayList<String>();

    public String getRecipeId() {
        return recipeId;
    }
    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getInstructions() {
        return instructions;
    }
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    public String getThumbnail() {
        return thumbnail;
    }
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
    public String getYoutubeLink() {
        return youtubeLink;
    }
    public void setYoutubeLink(String youtubeLink) {
        this.youtubeLink = youtubeLink;
    }
    public List<String> getIngredients() {
        return ingredients;
    }
    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
    public List<String> getMeasurements() {
        return measurements;
    }
    public void setMeasurements(List<String> measurements) {
        this.measurements = measurements;
    }
    public static Recipe convert(JsonObject jsonObject) {
        Recipe recipe = new Recipe();
        recipe.setRecipeId(jsonObject.getString("idMeal", ""));
        recipe.setName(jsonObject.getString("strMeal", ""));
        recipe.setCategory(jsonObject.getString("strCategory", ""));
        recipe.setCountry(jsonObject.getString("strArea", ""));
        recipe.setInstructions(jsonObject.getString("strInstructions", ""));
        recipe.setThumbnail(jsonObject.getString("strMealThumb", ""));
        String youtubeLink = jsonObject.getString("strYoutube", "");
        youtubeLink = youtubeLink.replace("/watch?v=", "/embed/");
        recipe.setYoutubeLink(youtubeLink);
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

    @Override
    public String toString() {
        return "\nName: %s\nCategory: %s\nCountry: %s\nInstructions: %s\nThumbnail: %s\nYoutubeLink: %s\nIngredients: ".formatted(name, category, country, instructions, thumbnail, youtubeLink) + ingredients + "\nMeasurements: " + measurements; 
    }
}
