package edu.nus.iss.sg.myrecipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.swing.plaf.metal.MetalBorders.PaletteBorder;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import edu.nus.iss.sg.myrecipe.models.Recipe;
import edu.nus.iss.sg.myrecipe.services.RecipeService;
import edu.nus.iss.sg.myrecipe.utils.ConversionUtils;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestRecipeController {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private RecipeService recipeSvc;

    @Test
    public void test1_shouldGetCreateRecipeForm() {
        final String expectedString = "Create Recipe";

        RequestBuilder req = MockMvcRequestBuilders.get("/account/recipe/create")
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
            assertTrue(payload.contains(expectedString));
        } catch (Exception ex) {
            fail("cannot retrieve response payload", ex);
            return;
        }
    }

    @Test
    public void test2_shouldPostCreateRecipeForm() {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("./src/main/resources/static/images/test.jpg");
        } catch (FileNotFoundException e1) {
            fail("File not found!");
            e1.printStackTrace();
        }
        
        MockMultipartFile multipartFile = null;
        try {
            multipartFile = new MockMultipartFile("recipeThumbnail", "test.jpg", MediaType.IMAGE_JPEG_VALUE, fis);
        } catch (IOException e1) {
            fail("Something went wrong when creating mock multipart file");
            e1.printStackTrace();
        }

        HashMap<String, String> contentTypeParams = new HashMap<String, String>();
        contentTypeParams.put("boundary", "265001916915724");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("recipeName", "Test Chicken Rice");
        params.add("recipeThumbnail", "test.jpg");
        params.add("recipeCategory", "Asian");
        params.add("recipeCountry", "Singapore");
        params.add("recipeInstructions", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        params.add("recipeYoutubeLink", "https://www.youtube.com/watch?v=XPA3rn1XImY");
        params.add("recipeMeasurement0", "1kg");
        params.add("recipeIngredient0", "Chicken");

        MediaType mediaType = new MediaType("multipart", "form-data",
        contentTypeParams);

        RequestBuilder req = null;
        req = MockMvcRequestBuilders.multipart("/account/recipe/create")
                    .file(multipartFile)
                    .params(params)
                    .param("usernameTest", "fred")
                    .contentType(mediaType)
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
            assertTrue(payload.contains("Recipe create successfully!"));
            assertNotNull(payload);
        } catch (Exception ex) {
            fail("cannot retrieve response payload", ex);
            return;
        }
    }

    @Test
    public void test3_shouldGetUserRecipeView() {
        
        RequestBuilder req = MockMvcRequestBuilders.get("/account/recipe/view")
                .param("usernameTest", "fred")
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
            assertTrue(payload.contains("Test Chicken Rice"));
            assertNotNull(payload);
        } catch (Exception ex) {
            fail("cannot retrieve response payload", ex);
            return;
        }
    }

    @Test
    public void test4_shouldShowDeleteConfirmation() {

        List<Recipe> recipes = recipeSvc.getUserRecipesByName("Test Chicken Rice");
        
        if(recipes.isEmpty()) {
            fail("No Test Chicken Rice found!");
        }

        RequestBuilder req = null;
        req = MockMvcRequestBuilders.post("/account/recipe/delete")
                    .content(ConversionUtils.buildUrlEncodedFormEntity(
                        "recipeIdToDelete", recipes.get(0).getRecipeId()
                    ))
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
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
            assertTrue(payload.contains("Are you sure?"));
            assertNotNull(payload);
        } catch (Exception ex) {
            fail("cannot retrieve response payload", ex);
            return;
        }
    }

    @Test
    public void test5_shouldNotDeleteRecipe() {
        List<Recipe> recipes = recipeSvc.getUserRecipesByName("Test Chicken Rice");
        
        if(recipes.isEmpty()) {
            fail("No Test Chicken Rice found!");
        }

        RequestBuilder req = null;
        req = MockMvcRequestBuilders.post("/account/recipe/delete")
                    .content(ConversionUtils.buildUrlEncodedFormEntity(
                        "recipeIdToDelete", recipes.get(0).getRecipeId()
                    ))
                    .param("no", "no")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .accept(MediaType.TEXT_HTML_VALUE);


        try {
            mvc.perform(req).andReturn();
        } catch (Exception ex) {
            fail("cannot perform mvc invocation", ex);
            return;
        }

        recipes = recipeSvc.getUserRecipesByName("Test Chicken Rice");
        assertFalse(recipes.isEmpty());

        String name = recipes.get(0).getName();
        assertEquals("Test Chicken Rice", name);
    }

    @Test
    public void test6_shouldDeleteRecipe() {
        List<Recipe> recipes = recipeSvc.getUserRecipesByName("Test Chicken Rice");
        
        if(recipes.isEmpty()) {
            fail("No Test Chicken Rice found!");
        }

        RequestBuilder req = null;
        req = MockMvcRequestBuilders.post("/account/recipe/delete")
                    .content(ConversionUtils.buildUrlEncodedFormEntity(
                        "recipeIdToDelete", recipes.get(0).getRecipeId()
                    ))
                    .param("yes", "yes")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
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
            assertTrue(payload.contains("Successfully deleted!"));
            assertNotNull(payload);
        } catch (Exception ex) {
            fail("cannot retrieve response payload", ex);
            return;
        }
    }
}
