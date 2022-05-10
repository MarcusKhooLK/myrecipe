package edu.nus.iss.sg.myrecipe;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import edu.nus.iss.sg.myrecipe.models.Recipe;
import edu.nus.iss.sg.myrecipe.services.SearchService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class TestSearchController {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private SearchService searchSvc;

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

}
