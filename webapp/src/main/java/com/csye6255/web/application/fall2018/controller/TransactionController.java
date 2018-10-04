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
import java.util.ArrayList;
import java.util.List;

/**
 * @author Akilan Rajendiran
 */

@Controller
public class TransactionController {

    @Autowired
    UserDAO userDao;

    @Autowired
    TransactionDAO transactionDAO;

    @RequestMapping(value = "/transaction", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity getTransactions(@RequestHeader HttpHeaders headers, HttpServletRequest request) {
        final String authorization = request.getHeader("Authorization");
        JsonObject jsonObject = new JsonObject();
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            String[] values = AuthorizationUtility.getHeaderValues(authorization);
            String userName = values[0];
            String password = values[1];
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            List<User> userList = userDao.findByUserName(userName);
            List<JsonObject> jsonObjectList = new ArrayList<>();

            if (userList.size() != 0) {
                User user = userList.get(0);
                if (encoder.matches(password, user.getPassword())) {
                    List<Transaction> transactionList = transactionDAO.findByUserId(user.getId());
                    if (((List) transactionList).size() != 0) {
                        for (Transaction transaction : transactionList) {
                            JsonObject jsonObject1 = new JsonObject();
                            jsonObject1.addProperty("id", transaction.getTransactionid());
                            jsonObject1.addProperty("description", transaction.getDescription());
                            jsonObject1.addProperty("merchant", transaction.getAmount());
                            jsonObject1.addProperty("amount", transaction.getDate());
                            jsonObject1.addProperty("date", transaction.getMerchant());
                            jsonObject1.addProperty("category", transaction.getCategory());
                            jsonObjectList.add(jsonObject1);
                        }
                        return ResponseEntity.status(HttpStatus.OK).body(jsonObjectList.toString());

                    } else {
                        jsonObject.addProperty("message", "There is no transactions to show");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(jsonObject.toString());
                    }

                } else {
                    jsonObject.addProperty("message", "Incorrect Password");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(jsonObject.toString());
                }


            } else {
                jsonObject.addProperty("message", "User not found! - Try Logging in again");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(jsonObject.toString());
            }

        } else {
            jsonObject.addProperty("message", "You are not logged in!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(jsonObject.toString());

        }


    }

    @RequestMapping(value = "/transaction", method = RequestMethod.POST, produces = {"application/json"},
            consumes = "application/json", headers = {"content-type=application/json; charset=utf-8"})
    @ResponseBody
    public ResponseEntity createTransactions(HttpServletRequest request, @RequestBody Transaction transaction) {
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
                    if (transaction.getDescription() != null && transaction.getAmount() != null
                            && transaction.getDate() != null && transaction.getMerchant() != null && transaction.getCategory() != null) {
                        Transaction t = new Transaction();
                        t.setTransactionid(transaction.getTransactionid());
                        t.setDescription(transaction.getDescription());
                        t.setAmount(transaction.getAmount());
                        t.setDate(transaction.getDate());
                        t.setMerchant(transaction.getMerchant());
                        t.setCategory(transaction.getCategory());
                        t.setUser(user);
                        transactionDAO.save(t);
                        jsonObject.addProperty("message", "Transaction  Successful");
                        return ResponseEntity.status(HttpStatus.CREATED).body(jsonObject.toString());
                    } else {
                        jsonObject.addProperty("message", "Transaction not successful - Provide id,desc,amount,date,merchant,category");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonObject.toString());

                    }
                } else {
                    jsonObject.addProperty("message", "Incorrect Password");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(jsonObject.toString());
                }


            } else {
                jsonObject.addProperty("message", "User not found! - Try Logging in again");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(jsonObject.toString());
            }

        } else {
            jsonObject.addProperty("message", "You are not logged in - Provide Username and Password!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(jsonObject.toString());

        }
    }

    @RequestMapping(value = "/transaction/{transactionid}", method = RequestMethod.DELETE, headers = {"content-type=application/json; charset=utf-8"})
    @ResponseBody
    public ResponseEntity<?> deleteTransactions(@PathVariable("transactionid") String transactionid, HttpServletRequest request) {
        final String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            String[] values = AuthorizationUtility.getHeaderValues(authorization);
            String userName = values[0];
            String password = values[1];
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            List<User> userList = userDao.findByUserName(userName);
            if (userList.size() != 0) {
                User user = userList.get(0);
                List<Transaction> transactionList = transactionDAO.findByTransactionid(transactionid);
                if (encoder.matches(password, user.getPassword())) {
                    if (transactionList.size() != 0) {
                        Transaction trans = transactionList.get(0);
                        if (trans.getUser().getId() == user.getId()) {
                            transactionDAO.delete(trans);
                            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
                        } else
                            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
                    } else
                        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                } else
                    return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            } else
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } else
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
    }

}
