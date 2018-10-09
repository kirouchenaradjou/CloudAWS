package com.csye6255.web.application.fall2018.controller;

import com.csye6255.web.application.fall2018.dao.AttachmentDAO;
import com.csye6255.web.application.fall2018.dao.TransactionDAO;
import com.csye6255.web.application.fall2018.dao.UserDAO;
import com.csye6255.web.application.fall2018.pojo.Attachment;
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
 * @author rkirouchenaradjou
 */
@Controller
public class AttachmentController {

    @Autowired
    UserDAO userDao;

    @Autowired
    TransactionDAO transactionDAO;

    @Autowired
    AttachmentDAO attachmentDAO;

    @RequestMapping(value = "/transaction/{transactionid}/attachments", method = RequestMethod.POST, produces = {"application/json"},
            consumes = "application/json", headers = {"content-type=application/json; charset=utf-8"})
    @ResponseBody
    public ResponseEntity attachFilesToTransaction(@PathVariable("transactionid") String transactionid,
                                                   HttpServletRequest request, @RequestBody Attachment attachment) {
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
                    List<Transaction> transactionList = transactionDAO.findByTransactionid(transactionid);
                    if (transactionList.size() != 0) {
                        Transaction trans = transactionList.get(0);
                        if (trans.getUser().getId() == user.getId()) {
                            if (attachment.getUrl() != null)  {
                                Attachment attachmentNew = new Attachment();
                                attachmentNew.setUrl(attachment.getUrl());
                                attachmentNew.setTransaction(trans);
                                attachmentDAO.save(attachmentNew);
                                jsonObject.addProperty("message", "File attached successfully");
                                return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
                            } else {
                                jsonObject.addProperty("message", "File could not be attached successfully - Please provide a url");
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonObject.toString());
                            }
                        } else
                            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                    } else
                        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
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

    @RequestMapping(value = "/transaction/{transactionid}/attachments", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity getAttachmentsByTransactionID(@PathVariable("transactionid") String transactionid, @RequestHeader HttpHeaders headers,
                                                        HttpServletRequest request) {
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
                    if (transactionList.size() != 0) {
                        Transaction trans = transactionList.get(0);
                        if (trans.getUser().getId() == user.getId()) {
                            List<Attachment> attachmentList = attachmentDAO.findByTransaction(transactionid);
                            if (attachmentList.size() != 0) {
                                for (Attachment attachment : attachmentList) {
                                    JsonObject jsonObject1 = new JsonObject();
                                    jsonObject.addProperty("id", attachment.getAttachmentid());
                                    jsonObject.addProperty("url", attachment.getUrl());
                                    jsonObjectList.add(jsonObject1);
                                }
                                return ResponseEntity.status(HttpStatus.OK).body(jsonObjectList.toString());
                            } else {
                                jsonObject.addProperty("message", "There are no attachments on this transaction");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(jsonObject.toString());
                            }
                        } else
                            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                    } else {
                        jsonObject.addProperty("message", "No transactions to show");
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

    @RequestMapping(value = "/transaction/{transactionid}/attachments/{attachmentid}",
            method = RequestMethod.DELETE, headers = {"content-type=application/json; charset=utf-8"})
    @ResponseBody
    public ResponseEntity<?> deleteFile(@PathVariable("transactionid") String transactionid, @PathVariable("attachmentid") String attachmentid,
                                        HttpServletRequest request) {
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
                        for (Transaction transaction : transactionList) {
                            if (transaction.getTransactionid() == transactionid) {
                                Attachment attachment = attachmentDAO.findByAttachmentid(attachmentid);
                                if(attachment != null)
                                {
                                    if (attachment.getAttachmentid() == attachmentid) {
                                        attachmentDAO.delete(attachment);
                                        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
                                    } else
                                        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                                }
                            } else
                                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                        }
                    } else
                        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
                } else
                    return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            } else
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } else
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);

    }

}
