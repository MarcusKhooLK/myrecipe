package edu.nus.iss.sg.myrecipe.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.nus.iss.sg.myrecipe.models.Recipe;
import edu.nus.iss.sg.myrecipe.repository.RecipeRepository;
import edu.nus.iss.sg.myrecipe.repository.UserRepository;

@Service
public class RecipeService {
    
    @Autowired
    private RecipeRepository recipeRepo;

    @Autowired
    private UserRepository userRepo;

    @Transactional
    public Boolean createRecipe(Recipe recipe, String username) {
        if(username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }

        Integer userId = userRepo.findUserIdByUsername(username);

        if(userId < 0) {
            throw new IllegalArgumentException();
        }

        Integer recipeId = recipeRepo.insertRecipe(recipe, userId);

        if(recipeId < 0) {
            throw new IllegalArgumentException();
        }

        recipe.setRecipeId(recipeId.toString());

        return recipeRepo.insertIngredients(recipe.getIngredients(), recipe.getMeasurements(), recipeId);
    }

    public List<Recipe> getAllUserRecipesByUserId(String username) {
        Integer userId = userRepo.findUserIdByUsername(username);

        if(userId < 0) {
            return new ArrayList<Recipe>();
        }

        return recipeRepo.getAllUserRecipesByUserId(userId);
    }

    public List<Recipe> getUserRecipesByName(String recipeName) {
        return recipeRepo.getUserRecipesByName(recipeName);
    }

    public List<Recipe> getRecipesByCategory(String category) {
        return recipeRepo.getRecipesByCategory(category);
    }

    public List<Recipe> getRecipesByArea(String area) {
        return recipeRepo.getRecipesByArea(area);
    }

    public Optional<Recipe> getRecipeByRecipeId(Integer recipeId) {
        return recipeRepo.getRecipeByRecipeId(recipeId);
    }

    @Transactional
    public Boolean deleteRecipeByRecipeId(Integer recipeId) {

        if(recipeRepo.deleteIngredientsByRecipeId(recipeId)) {
            return recipeRepo.deleteRecipeByRecipeId(recipeId);
        }

        return false;
    }
}
