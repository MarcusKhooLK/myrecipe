package edu.nus.iss.sg.myrecipe;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import edu.nus.iss.sg.myrecipe.models.Recipe;
import edu.nus.iss.sg.myrecipe.services.RecipeService;
import edu.nus.iss.sg.myrecipe.services.SearchService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class TestSearchController {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private SearchService searchSvc;

    @Autowired
    private RecipeService recipeSvc;

    @Test
    public void test1_shouldSearchByString() {
        final String query = "pasta";
        final String expectedReturnString = "Mediterranean Pasta Salad";

        RequestBuilder req = MockMvcRequestBuilders.get("/search")
                .queryParam("s", query)
                .accept(MediaType.TEXT_HTML_VALUE);

        MvcResult result = null;

        try {
            result = mvc.perform(req).andReturn();
        } catch (Exception ex) {
            fail("cannot perform mvc invocation", ex);
            return;
        }

        MockHttpServletResponse resp = result.getResponse();
        try {
            String payload = resp.getContentAsString();
            assertNotNull(payload);
            assertTrue(payload.contains(expectedReturnString));
        } catch (Exception ex) {
            fail("cannot retrieve response payload", ex);
            return;
        }
    }

    @Test
    public void test2_shouldSearchByRecipeId() {
        final String query = "pasta";
        final String expectedReturnString = "Mediterranean Pasta Salad";

        List<Recipe> recipes = searchSvc.searchRecipesFromMealDb(query);

        if (recipes.isEmpty())
            fail("Cannot find pasta");

        String recipeId = recipes.get(0).getRecipeId();

        RequestBuilder req = MockMvcRequestBuilders.get("/search/%s".formatted(recipeId))
                .accept(MediaType.TEXT_HTML_VALUE);

        MvcResult result = null;

        try {
            result = mvc.perform(req).andReturn();
        } catch (Exception ex) {
            fail("cannot perform mvc invocation", ex);
            return;
        }

        MockHttpServletResponse resp = result.getResponse();
        try {
            String payload = resp.getContentAsString();
            assertNotNull(payload);
            assertTrue(payload.contains(expectedReturnString));
        } catch (Exception ex) {
            fail("cannot retrieve response payload", ex);
            return;
        }
    }

    @Test
    public void test3_shouldSearchUsingMyRecipeDb() {
        // insert recipe
        Recipe r = new Recipe();
        r.setCategory("Asian");
        r.setCountry("Singapore");
        r.setCreatedBy("fred");
        r.setIngredients( new ArrayList<>() {{ add("Chicken"); }});
        r.setInstructions("Testing");
        r.setMeasurements(new ArrayList<>() {{ add("1kg"); }});
        r.setName("Test Chicken Rice");

        recipeSvc.createRecipe(r, "fred");

        List<Recipe> recipes = searchSvc.searchRecipesFromMyRecipeDb("Test Chicken Rice");

        if (recipes.isEmpty())
            fail("Cannot find Test Chicken Rice");

        String recipeId = recipes.get(0).getRecipeId();

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("name", "fred");

        RequestBuilder req = MockMvcRequestBuilders.get("/search/u/%s".formatted(recipeId))
                .session(session)
                .accept(MediaType.TEXT_HTML_VALUE);

        MvcResult result = null;

        try {
            result = mvc.perform(req).andReturn();
        } catch (Exception ex) {
            fail("cannot perform mvc invocation", ex);
            return;
        }

        MockHttpServletResponse resp = result.getResponse();
        try {
            String payload = resp.getContentAsString();
            assertNotNull(payload);
            assertTrue(payload.contains("Test Chicken Rice"));
        } catch (Exception ex) {
            fail("cannot retrieve response payload", ex);
            return;
        }

        recipeSvc.deleteRecipeByRecipeId(Integer.parseInt(recipeId));
    }

    @Test
    public void test4_shouldReturnPageNotFoundByRecipeId_NumberFormatException() {
        final String recipeId = "pasta";
        RequestBuilder req = MockMvcRequestBuilders.get("/search/%s".formatted(recipeId))
                .accept(MediaType.TEXT_HTML_VALUE);

        MvcResult result = null;

        try {
            result = mvc.perform(req).andReturn();
        } catch (Exception ex) {
            fail("cannot perform mvc invocation", ex);
            return;
        }

        MockHttpServletResponse resp = result.getResponse();
        try {
            String payload = resp.getContentAsString();
            assertNotNull(payload);
            assertTrue(payload.contains("Page not found!"));
        } catch (Exception ex) {
            fail("cannot retrieve response payload", ex);
            return;
        }
    }

    @Test
    public void test5_shouldReturnPageNotFoundByRecipId_InvalidId() {
        final String recipeId = "1";
        RequestBuilder req = MockMvcRequestBuilders.get("/search/%s".formatted(recipeId))
                .accept(MediaType.TEXT_HTML_VALUE);

        MvcResult result = null;

        try {
            result = mvc.perform(req).andReturn();
        } catch (Exception ex) {
            fail("cannot perform mvc invocation", ex);
            return;
        }

        MockHttpServletResponse resp = result.getResponse();
        try {
            String payload = resp.getContentAsString();
            assertNotNull(payload);
            assertTrue(payload.contains("Page not found!"));
        } catch (Exception ex) {
            fail("cannot retrieve response payload", ex);
            return;
        }
    }

    @Test
    public void test6_shouldSearchMealDBWithCategoryFilter() {
        String categoryName = "beef";
        String expectedReturnString = "Beef and Mustard Pie";
        RequestBuilder req = MockMvcRequestBuilders.get("/search/category/%s".formatted(categoryName))
                .accept(MediaType.TEXT_HTML_VALUE);

        MvcResult result = null;

        try {
            result = mvc.perform(req).andReturn();
        } catch (Exception ex) {
            fail("cannot perform mvc invocation", ex);
            return;
        }

        MockHttpServletResponse resp = result.getResponse();
        try {
            String payload = resp.getContentAsString();
            assertNotNull(payload);
            assertTrue(payload.contains(expectedReturnString));
        } catch (Exception ex) {
            fail("cannot retrieve response payload", ex);
            return;
        }
    }

    @Test
    public void test7_shouldSearchMealDBWithAreaFilter(){
        String categoryName = "american";
        String expectedReturnString = "Banana Pancakes";
        RequestBuilder req = MockMvcRequestBuilders.get("/search/area/%s".formatted(categoryName))
                .accept(MediaType.TEXT_HTML_VALUE);

        MvcResult result = null;

        try {
            result = mvc.perform(req).andReturn();
        } catch (Exception ex) {
            fail("cannot perform mvc invocation", ex);
            return;
        }

        MockHttpServletResponse resp = result.getResponse();
        try {
            String payload = resp.getContentAsString();
            assertNotNull(payload);
            assertTrue(payload.contains(expectedReturnString));
        } catch (Exception ex) {
            fail("cannot retrieve response payload", ex);
            return;
        }
    }
    
    @Test
    public void test8_shouldReturnEmpty() {
        List<Recipe> recipes = searchSvc.searchRecipesFromMealDb("!@)#*)%&");
        assertTrue(recipes.isEmpty());

        Optional<Recipe> recipe = searchSvc.searchRecipeFromMyRecipeDbById(-1);
        assertTrue(recipe.isEmpty());

        recipes = searchSvc.searchRecipesFromMealDbWithFilter("c", "dummy");
        assertTrue(recipes.isEmpty());
    }

}
