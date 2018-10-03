package com.csye6255.web.application.fall2018.controller;

import com.csye6255.web.application.fall2018.dao.TransactionDAO;
import com.csye6255.web.application.fall2018.dao.UserDAO;
import com.csye6255.web.application.fall2018.pojo.Transaction;
import com.csye6255.web.application.fall2018.pojo.User;

/**
        * @author Akilan Rajendiran
        *
        */

public class TransactionController {

    UserDAO userDao;
    TransactionDAO transactionDAO;

    @RequestMapping(value = "/transaction", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getTransactions(@RequestHeader HttpHeaders headers, HttpServletRequest request)
    {
        JsonObject jsonObject = new JsonObject();
        final String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = authorization.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
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
                            jsonObject.addProperty("id",transaction.getId());
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

    @RequestMapping(value = "/transaction", method = RequestMethod.POST, produces = "application/json")
    consumes = "application/json", headers = {"content-type=application/json; charset=utf-8"})
    @ResponseBody
    public String createTransactions(@RequestHeader HttpHeaders headers, HttpServletRequest request,Transaction jsonString)
    {
        JsonObject jsonObject = new JsonObject();
        final String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = authorization.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            String userName = values[0];
            String password = values[1];
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            List<User> userList = userDao.findByUserName(userName);


            if (userList.size() != 0) {
                User user = userList.get(0);
                if (encoder.matches(password, user.getPassword())) {
                    // jsonObject.addProperty("message", "Current Time is : "+ new Date().toString());
                    Transaction transactionNew = new Transaction();
                    transactionNew.setId(jsonString.getId());
                    transactionNew.setDescription(jsonString.getDescription());
                    transactionNew.setAmount(jsonString.getAmount());
                    transactionNew.setDate(jsonString.getDate());
                    transactionNew.setMerchant(jsonString.getMerchant());
                    transactionNew.setCatergory(jsonString.getCatergory());
                    transactionDAO.save(transactionNew);
                    jsonObject.addProperty("message", "Transaction  Successful");


                } else jsonObject.addProperty("message", "Incorrect Password");


            } else jsonObject.addProperty("message", "User not found! - Try Logging in again");

        }

        else
            jsonObject.addProperty("message","You are not logged in!");


        return jsonObject.toString();

    }

}
