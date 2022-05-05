package edu.nus.iss.sg.myrecipe.models;

import java.util.ArrayList;
import java.util.List;

public class Recipe {

    private String recipeId;
    private String name;
    private String category;
    private String country;
    private String instructions;
    private String thumbnail;
    private String youtubeLink;
    private String createdBy;
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
    public String getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "\nName: %s\nCategory: %s\nCountry: %s\nInstructions: %s\nThumbnail: %s\nYoutubeLink: %s\nIngredients: ".formatted(name, category, country, instructions, thumbnail, youtubeLink) + ingredients + "\nMeasurements: " + measurements; 
    }
}
