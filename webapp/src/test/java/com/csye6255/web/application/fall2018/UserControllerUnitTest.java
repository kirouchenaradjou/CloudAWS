package com.csye6255.web.application.fall2018.controller;

import com.csye6255.web.application.fall2018.dao.UserDAO;
import com.csye6255.web.application.fall2018.pojo.User;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author rkirouchenaradjou
 */
@RunWith(SpringRunner.class)
@WebMvcTest(value = UserController.class, secure = false)
public class UserControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDAO userDAO;


    String exampleUserJson = "{\"userName\":\"user1@gmail.com\",\"password\":\"user1\"}";

    @Before
    public void setUp(){

    }

    @Test
    public void userRegistrationTestWithUserAlreadyExists(){
        User user = new User();
        user.setUserName("user1@gmail.com");
        user.setPassword("user1");

        List<User> test = new ArrayList<>();
        test.add(user);
        Mockito.when(
                userDAO.findByUserName(Mockito.anyString())).thenReturn(test);

        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/user/register").
                        accept(MediaType.APPLICATION_JSON).content(exampleUserJson).
                        contentType(MediaType.APPLICATION_JSON);

        MvcResult result;
        assertResponse(requestBuilder, "User already exists!");


    }

    private void assertResponse(RequestBuilder requestBuilder, String s) {
        MvcResult result;
        try {
            result = mockMvc.perform(requestBuilder).andReturn();
            MockHttpServletResponse response = result.getResponse();
            JSONObject myObject = new JSONObject(response.getContentAsString());

            assertEquals(myObject.get("message"), s);
            assertEquals(HttpStatus.OK.value(), response.getStatus());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void userRegistrationWithNewAccount(){
        User user = new User();
        user.setUserName("user2@gmail.com");
        user.setPassword("user2");

        List<User> test = new ArrayList<>();
        Mockito.when(
                userDAO.findByUserName(Mockito.anyString())).thenReturn(test);

        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/user/register").
                        accept(MediaType.APPLICATION_JSON).content(exampleUserJson).
                        contentType(MediaType.APPLICATION_JSON);

        MvcResult result;
        assertResponse(requestBuilder, "Registration Successful");


    }

}
