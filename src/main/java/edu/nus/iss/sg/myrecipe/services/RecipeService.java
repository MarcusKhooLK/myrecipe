package edu.nus.iss.sg.myrecipe.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.nus.iss.sg.myrecipe.models.Recipe;
import edu.nus.iss.sg.myrecipe.repository.RecipeRepository;
import edu.nus.iss.sg.myrecipe.repository.UserRepository;

@Service
public class RecipeService {
    
    @Autowired
    private RecipeRepository recipeRepo;

    @Autowired
    private UserRepository userRepo;

    public void createRecipe(Recipe recipe, String username) {
        recipeRepo.insertRecipe(recipe, user_id)
    }
}
