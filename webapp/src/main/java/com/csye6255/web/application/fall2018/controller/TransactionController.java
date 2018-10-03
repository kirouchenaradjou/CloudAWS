package com.csye6255.web.application.fall2018.controller;

import com.csye6255.web.application.fall2018.dao.TransactionDAO;
import com.csye6255.web.application.fall2018.dao.UserDAO;
import com.csye6255.web.application.fall2018.pojo.Transaction;
import com.csye6255.web.application.fall2018.pojo.User;
import com.csye6255.web.application.fall2018.utilities.AuthorizationUtility;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * @author Akilan Rajendiran
 *
 */

@Controller
public class TransactionController {

    @Autowired
    UserDAO userDao;

    @Autowired
    TransactionDAO transactionDAO;

    @RequestMapping(value = "/transaction", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getTransactions(@RequestHeader HttpHeaders headers, HttpServletRequest request)
    {
        JsonObject jsonObject = new JsonObject();
        final String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            String[] values = AuthorizationUtility.getHeaderValues(authorization);
            String userName = values[0];
            String password = values[1];
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            List<User> userList = userDao.findByUserName(userName);


            if (userList.size() != 0) {
                User user = userList.get(0);
                if (encoder.matches(password, user.getPassword())) {
                    // jsonObject.addProperty("message", "Current Time is : "+ new Date().toString());

                    List<Transaction> transactionList=transactionDAO.findByUserId(user.getId());


                    if( ((List) transactionList).size()!=0){
                        for (Transaction transaction : transactionList) {
                            //jsonObject.addProperty("id",transaction.get());
                            jsonObject.addProperty("id",transaction.getDescription());
                            jsonObject.addProperty("id",transaction.getAmount());
                            jsonObject.addProperty("id",transaction.getDate());
                            jsonObject.addProperty("id",transaction.getMerchant());
                            jsonObject.addProperty("id",transaction.getCatergory());
                        }
                    }
                    else
                    {
                        jsonObject.addProperty("message", "There is no transactions to show");
                    }

                } else jsonObject.addProperty("message", "Incorrect Password");


            } else jsonObject.addProperty("message", "User not found! - Try Logging in again");

        }

        else
            jsonObject.addProperty("message","You are not logged in!");


        return jsonObject.toString();

    }

    @RequestMapping(value = "/transaction", method = RequestMethod.POST, produces = {"application/json"},
            consumes = "application/json", headers = {"content-type=application/json; charset=utf-8"})
    @ResponseBody
    public ResponseEntity createTransactions(HttpServletRequest request,@RequestBody Transaction transaction)
    {
        JsonObject jsonObject = new JsonObject();
        final String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            String[] values = AuthorizationUtility.getHeaderValues(authorization);
            String userName = values[0];
            String password = values[1];
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            List<User> userList = userDao.findByUserName(userName);


            if (userList.size() != 0) {
                User user = userList.get(0);
                if (encoder.matches(password, user.getPassword())) {
                    Transaction t = new Transaction();
                    t.setTransactionid(transaction.getTransactionid());
                    t.setDescription(transaction.getDescription());
                    t.setAmount(transaction.getAmount());
                    t.setDate(transaction.getDate());
                    t.setMerchant(transaction.getMerchant());
                    t.setCatergory(transaction.getCatergory());
                    t.setUser(user);
                    transactionDAO.save(t);
                    jsonObject.addProperty("message", "Transaction  Successful");
                    return ResponseEntity.status(HttpStatus.CREATED).body(jsonObject.toString());


                } else {
                    jsonObject.addProperty("message", "Incorrect Password");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(jsonObject.toString());
                }


            } else {
                jsonObject.addProperty("message", "User not found! - Try Logging in again");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(jsonObject.toString());
            }

        }

        else {
            jsonObject.addProperty("message","You are not logged in - Provide Username and Password!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(jsonObject.toString());

        }

    }

}
