package com.csye6255.web.application.fall2018;

import com.csye6255.web.application.fall2018.controller.TransactionController;
import com.csye6255.web.application.fall2018.controller.UserController;
import com.csye6255.web.application.fall2018.dao.TransactionDAO;
import com.csye6255.web.application.fall2018.dao.UserDAO;
import com.csye6255.web.application.fall2018.pojo.User;
import com.csye6255.web.application.fall2018.utilities.AuthorizationUtility;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * @author rkirouchenaradjou
 */
@RunWith(SpringRunner.class)
@WebMvcTest(value = TransactionController.class, secure = false)
public class TransactionControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDAO userDAO;

    @MockBean
    private TransactionDAO transactionDAO;


    @Before
    public void setUp(){

    }

    @Test
    public void createTransactionSuccessTest(){
        User user = new User();
        user.setUserName("raghavi@gmail.com");
        user.setPassword("$2a$10$3AyqVnqtp3icOoy9AIhuG.eWbVr62bCWvVin9.bPWJh6jZLcjBiH.");

        String examplePostTransactionJson = "{\n" +
                "  \"id\": \"d290f1ee-6c54-4b01-90e6-d701748f0851\",\n" +
                "  \"description\": \"chai1\",\n" +
                "  \"merchant\": \"dunkin2\",\n" +
                "  \"amount\": 2.02,\n" +
                "  \"date\": \"09/2/2019\",\n" +
                "  \"category\": \"2snacks\"\n" +
                "}";

        List<User> test = new ArrayList<>();
        test.add(user);
        BCryptPasswordEncoder tc = spy(new BCryptPasswordEncoder());
        Mockito.when(tc.matches("raghavi", "$2a$10$3AyqVnqtp3icOoy9AIhuG.eWbVr62bCWvVin9.bPWJh6jZLcjBiH.")).thenReturn(true);
        Mockito.when(
                userDAO.findByUserName(Mockito.anyString())).thenReturn(test);
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/transaction/").
                        accept(MediaType.APPLICATION_JSON).content(examplePostTransactionJson).
                        contentType(MediaType.APPLICATION_JSON).header("Authorization","Basic cmFnaGF2aUBnbWFpbC5jb206cmFnaGF2aQ==");

        MvcResult result;
        try {
            result = mockMvc.perform(requestBuilder).andReturn();
            MockHttpServletResponse response = result.getResponse();
            JSONObject myObject = new JSONObject(response.getContentAsString());

            assertEquals(myObject.get("message"), "Transaction  Successful");
            assertEquals(HttpStatus.CREATED.value(), response.getStatus());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Test
    public void createTransactionBadRequestTest(){
        User user = new User();
        user.setUserName("raghavi@gmail.com");
        user.setPassword("$2a$10$3AyqVnqtp3icOoy9AIhuG.eWbVr62bCWvVin9.bPWJh6jZLcjBiH.");

        String examplePostTransactionJson = "{\n" +
                "  \"id\": \"d290f1ee-6c54-4b01-90e6-d701748f0851\",\n" +
                "  \"merchant\": \"dunkin2\",\n" +
                "  \"amount\": 2.02,\n" +
                "  \"category\": \"2snacks\"\n" +
                "}";

        List<User> test = new ArrayList<>();
        test.add(user);
        BCryptPasswordEncoder tc = spy(new BCryptPasswordEncoder());
        Mockito.when(tc.matches("raghavi", "$2a$10$3AyqVnqtp3icOoy9AIhuG.eWbVr62bCWvVin9.bPWJh6jZLcjBiH.")).thenReturn(true);
        Mockito.when(
                userDAO.findByUserName(Mockito.anyString())).thenReturn(test);
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/transaction/").
                        accept(MediaType.APPLICATION_JSON).content(examplePostTransactionJson).
                        contentType(MediaType.APPLICATION_JSON).header("Authorization","Basic cmFnaGF2aUBnbWFpbC5jb206cmFnaGF2aQ==");

        MvcResult result;
        try {
            result = mockMvc.perform(requestBuilder).andReturn();
            MockHttpServletResponse response = result.getResponse();
            JSONObject myObject = new JSONObject(response.getContentAsString());

            assertEquals(myObject.get("message"), "Transaction not successful - Provide id,desc,amount,date,merchant,category");
            assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }



}
