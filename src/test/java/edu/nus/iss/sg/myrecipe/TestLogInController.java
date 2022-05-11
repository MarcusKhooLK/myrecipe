package edu.nus.iss.sg.myrecipe;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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

import edu.nus.iss.sg.myrecipe.repository.UserRepository;
import edu.nus.iss.sg.myrecipe.services.LogInService;
import edu.nus.iss.sg.myrecipe.utils.ConversionUtils;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestLogInController {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private LogInService logInSvc;

    @Autowired
    private UserRepository userRepo;

    private final String username = "dummy";
    private final String password = "dummy";

    @Test
    public void test1_shouldGetLogIn() {
        RequestBuilder req = MockMvcRequestBuilders.get("/login")
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
            assertTrue(payload.contains("Log In"));
            assertNotNull(payload);
        } catch (Exception ex) {
            fail("cannot retrieve response payload", ex);
            return;
        }
    }

    @Test
    public void test2_shouldCreateAccount() {
        RequestBuilder req = MockMvcRequestBuilders.post("/login/create")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(ConversionUtils.buildUrlEncodedFormEntity("usernameNew", username, "passwordNew", password));

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
        } catch (Exception ex) {
            fail("cannot retrieve response payload", ex);
            return;
        }

        Integer userId = userRepo.findUserIdByUsername(username);

        assertTrue(userId > 0);
    }

    @Test
    public void test3_shouldFailDuplicateAccount() {
        RequestBuilder req = MockMvcRequestBuilders.post("/login/create")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(ConversionUtils.buildUrlEncodedFormEntity("usernameNew", username, "passwordNew", password));

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
            assertTrue(payload.contains("Username is taken!"));
            assertNotNull(payload);
        } catch (Exception ex) {
            fail("cannot retrieve response payload", ex);
            return;
        }
    }

    @Test
    public void test4_shouldAuthenticateAccount() {
        RequestBuilder req = MockMvcRequestBuilders.post("/login/auth")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(ConversionUtils.buildUrlEncodedFormEntity("username", username, "password", password));

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
        } catch (Exception ex) {
            fail("cannot retrieve response payload", ex);
            return;
        }
    }

    @Test
    public void test5_shouldFailAuthAccount() {
        RequestBuilder req = MockMvcRequestBuilders.post("/login/auth")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(ConversionUtils.buildUrlEncodedFormEntity("username", username, "password", "123"));

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
            assertTrue(payload.contains("Log in failed"));
        } catch (Exception ex) {
            fail("cannot retrieve response payload", ex);
            return;
        }
    }

    @Test
    public void test6_shouldLogout() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("name", username);

        RequestBuilder req = MockMvcRequestBuilders.post("/login/logout")
                .session(session);

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
            assertTrue(payload.contains("My Recipe"));
        } catch (Exception ex) {
            fail("cannot retrieve response payload", ex);
            return;
        }
    }

    @Test
    public void test7_shouldRemoveAccount() {
        assertTrue(logInSvc.deleteAccount(username));
        assertFalse(logInSvc.authAccount(username, password));
    }
}
