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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
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
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestRecipeController {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private RecipeService recipeSvc;

    private MultiValueMap<String, String> testForm = new LinkedMultiValueMap<>();
    private HashMap<String, String> boundary = new HashMap<String, String>();
    private MockMultipartFile testThumbnail = null;
    private MockMultipartFile testThumbnail2 = null;
    private MockHttpSession testSession = new MockHttpSession();

    private final String testRecipeName = "Test Chicken Rice";
    private final String testRecipeCategory = "Asian";
    private final String testRecipeArea = "Singaporean";

    @BeforeAll
    public void setup() {
        testForm.add("recipeName", testRecipeName);
        testForm.add("recipeThumbnail", "test.jpg");
        testForm.add("recipeCategory", testRecipeCategory);
        testForm.add("recipeArea", testRecipeArea);
        testForm.add("recipeInstructions", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        testForm.add("recipeYoutubeLink", "https://www.youtube.com/watch?v=XPA3rn1XImY");
        testForm.add("recipeMeasurement0", "1kg");
        testForm.add("recipeIngredient0", "Chicken");

        boundary.put("boundary", "265001916915724");

        FileInputStream fis = null;
        try {
            fis = new FileInputStream("./src/main/resources/static/images/test.jpg");
        } catch (FileNotFoundException e1) {
            fail("File not found!");
            e1.printStackTrace();
        }
        
        try {
            testThumbnail = new MockMultipartFile("recipeThumbnail", "test.jpg", MediaType.IMAGE_JPEG_VALUE, fis);
        } catch (IOException e1) {
            fail("Something went wrong when creating mock multipart file");
            e1.printStackTrace();
        }

        try {
            fis = new FileInputStream("./src/main/resources/static/images/test2.jpg");
        } catch (FileNotFoundException e1) {
            fail("File not found!");
            e1.printStackTrace();
        }
        
        try {
            testThumbnail2 = new MockMultipartFile("recipeThumbnail", "test2.jpg", MediaType.IMAGE_JPEG_VALUE, fis);
        } catch (IOException e1) {
            fail("Something went wrong when creating mock multipart file");
            e1.printStackTrace();
        }

        testSession.setAttribute("name", "fred");
    }

    @Test
    @Order(1)
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
    @Order(2) 
    public void test2_shouldPostCreateRecipeForm() {
        MediaType mediaType = new MediaType("multipart", "form-data", boundary);

        RequestBuilder req = null;
        req = MockMvcRequestBuilders.multipart("/account/recipe/create")
                    .file(testThumbnail)
                    .session(testSession)
                    .params(testForm)
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
            assertNotNull(payload);
            assertTrue(payload.contains("Recipe created successfully!"));
        } catch (Exception ex) {
            fail("cannot retrieve response payload", ex);
            return;
        }
    }

    @Test
    @Order(3) 
    public void test3_shouldShowEditRecipeForm() {
        final String expectedString = "Edit Recipe";

        List<Recipe> recipes = recipeSvc.getUserRecipesByName(testRecipeName);
        assertFalse(recipes.isEmpty());

        String recipeIdStr = recipes.get(0).getRecipeId();

        RequestBuilder req = MockMvcRequestBuilders.post("/account/recipe/edit")
                .content(ConversionUtils.buildUrlEncodedFormEntity(
                    "recipeIdToEdit", recipeIdStr
                ))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .session(testSession)
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
    @Order(4) 
    public void test4_shouldNotShowEditRecipeForm() {
        RequestBuilder req = MockMvcRequestBuilders.post("/account/recipe/edit")
                .content(ConversionUtils.buildUrlEncodedFormEntity(
                    "recipeIdToEdit", "-1"
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
            assertNotNull(payload);
            assertTrue(payload.contains("Something went wrong when editing recipe!"));
        } catch (Exception ex) {
            fail("cannot retrieve response payload", ex);
            return;
        }
    }

    @Test
    @Order(5) 
    public void test5_shouldNotUpdateRecipe_RecipeNotFound() {
        MultiValueMap<String, String> newForm = new LinkedMultiValueMap<>();
        newForm.addAll(testForm);
        newForm.add("recipeIdToEdit", "-1");

        MediaType mediaType = new MediaType("multipart", "form-data", boundary);

        RequestBuilder req = null;
        req = MockMvcRequestBuilders.multipart("/account/recipe/edit")
                    .file(testThumbnail2)
                    .session(testSession)
                    .params(newForm)
                    .param("done", "done")
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
            assertNotNull(payload);
            assertTrue(payload.contains("Something went wrong when editing recipe! Recipe not found!"));
        } catch (Exception ex) {
            fail("cannot retrieve response payload", ex);
            return;
        }
    }

    @Test
    @Order(6) 
    public void test6_shouldNotUpdateRecipe_InvalidUser() {
        List<Recipe> recipes = recipeSvc.getUserRecipesByName(testRecipeName);
        assertFalse(recipes.isEmpty());

        String recipeIdStr = recipes.get(0).getRecipeId();

        MultiValueMap<String, String> newForm = new LinkedMultiValueMap<>();
        newForm.addAll(testForm);
        newForm.add("recipeIdToEdit", recipeIdStr);

        MediaType mediaType = new MediaType("multipart", "form-data", boundary);

        RequestBuilder req = null;
        req = MockMvcRequestBuilders.multipart("/account/recipe/edit")
                    .file(testThumbnail2)
                    .params(newForm)
                    .param("done", "done")
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
            assertNotNull(payload);
            assertTrue(payload.contains("Something went wrong when editing recipe"));
        } catch (Exception ex) {
            fail("cannot retrieve response payload", ex);
            return;
        }
    }

    @Test
    @Order(7) 
    public void test7_shouldUpdateRecipe() {
        final String newArea = "Malaysian";
        final String newCategory = "Chicken";

        List<Recipe> recipes = recipeSvc.getUserRecipesByName(testRecipeName);
        assertFalse(recipes.isEmpty());

        String recipeIdStr = recipes.get(0).getRecipeId();

        MultiValueMap<String, String> newForm = new LinkedMultiValueMap<>();
        newForm.addAll(testForm);
        newForm.add("recipeIdToEdit", recipeIdStr);
        newForm.set("recipeArea", "Malaysian");
        newForm.set("recipeCategory", "Chicken");

        MediaType mediaType = new MediaType("multipart", "form-data", boundary);

        RequestBuilder req = null;
        req = MockMvcRequestBuilders.multipart("/account/recipe/edit")
                    .file(testThumbnail2)
                    .session(testSession)
                    .params(newForm)
                    .param("done", "done")
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
            assertNotNull(payload);
            assertTrue(payload.contains("Recipe updated successfully!"));
        } catch (Exception ex) {
            fail("cannot retrieve response payload", ex);
            return;
        }

        recipes = recipeSvc.getUserRecipesByName(testRecipeName);
        assertFalse(recipes.isEmpty());

        String cat = recipes.get(0).getCategory();
        assertEquals(newCategory, cat);

        String area = recipes.get(0).getCountry();
        assertEquals(newArea, area);
    }

    @Test
    @Order(8) 
    public void test8_shouldGetUserRecipeView() {
        RequestBuilder req = MockMvcRequestBuilders.get("/account/recipe/view")
                .session(testSession)
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
    }

    @Test
    @Order(9) 
    public void test9_shouldShowDeleteConfirmation() {

        List<Recipe> recipes = recipeSvc.getUserRecipesByName(testRecipeName);
        
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
            assertNotNull(payload);
            assertTrue(payload.contains("Are you sure?"));
        } catch (Exception ex) {
            fail("cannot retrieve response payload", ex);
            return;
        }
    }

    @Test
    @Order(10) 
    public void test10_shouldNotDeleteRecipe() {
        List<Recipe> recipes = recipeSvc.getUserRecipesByName(testRecipeName);
        
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

        recipes = recipeSvc.getUserRecipesByName(testRecipeName);
        assertFalse(recipes.isEmpty());

        String name = recipes.get(0).getName();
        assertEquals(testRecipeName, name);
    }

    @Test
    @Order(11) 
    public void test11_shouldNotDeleteRecipe_NumberFormatException() {
        RequestBuilder req = null;
        req = MockMvcRequestBuilders.post("/account/recipe/delete")
                    .content(ConversionUtils.buildUrlEncodedFormEntity(
                        "recipeIdToDelete", "Test Chicken Rice"
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
            assertNotNull(payload);
            assertTrue(payload.contains("Something went wrong!"));
        } catch (Exception ex) {
            fail("cannot retrieve response payload", ex);
            return;
        }

        List<Recipe> recipes = recipeSvc.getUserRecipesByName(testRecipeName);
        assertFalse(recipes.isEmpty());

        String name = recipes.get(0).getName();
        assertEquals(testRecipeName, name);
    }

    @Test
    @Order(12) 
    public void test12_shouldNotDeleteRecipe_RecipeNotFound() {
        RequestBuilder req = null;
        req = MockMvcRequestBuilders.post("/account/recipe/delete")
                    .content(ConversionUtils.buildUrlEncodedFormEntity(
                        "recipeIdToDelete", "-1"
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
            assertNotNull(payload);
            assertTrue(payload.contains("Something went wrong! Recipe not found!"));
        } catch (Exception ex) {
            fail("cannot retrieve response payload", ex);
            return;
        }

        List<Recipe> recipes = recipeSvc.getUserRecipesByName(testRecipeName);
        assertFalse(recipes.isEmpty());

        String name = recipes.get(0).getName();
        assertEquals(testRecipeName, name);
    }

    @Test
    @Order(13) 
    public void test13_shouldDeleteRecipe() {
        List<Recipe> recipes = recipeSvc.getUserRecipesByName(testRecipeName);
        
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
            assertNotNull(payload);
            assertTrue(payload.contains("Successfully deleted!"));
        } catch (Exception ex) {
            fail("cannot retrieve response payload", ex);
            return;
        }

        recipes = recipeSvc.getUserRecipesByName(testRecipeName);
        assertTrue(recipes.isEmpty());
    }

    @Test
    @Order(14) 
    public void test14_shouldNotCreateRecipeWithoutLogIn() {

        MediaType mediaType = new MediaType("multipart", "form-data",
        boundary);

        RequestBuilder req = null;
        req = MockMvcRequestBuilders.multipart("/account/recipe/create")
                    .file(testThumbnail)
                    .params(testForm)
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
            assertNotNull(payload);
            assertTrue(payload.contains("Something went wrong when creating recipe"));
        } catch (Exception ex) {
            fail("cannot retrieve response payload", ex);
            return;
        }
    }

    @AfterAll
    public void teardown() {
        List<Recipe> recipes = recipeSvc.getUserRecipesByName(testRecipeName);
        if(!recipes.isEmpty()) {
            Integer recipeId = Integer.parseInt(recipes.get(0).getRecipeId());
            recipeSvc.deleteRecipeByRecipeId(recipeId);
        }

        testForm = new LinkedMultiValueMap<>();
        boundary = new HashMap<String, String>();
        testThumbnail = null;
        testThumbnail2 = null;
        testSession.invalidate();
    }
}
