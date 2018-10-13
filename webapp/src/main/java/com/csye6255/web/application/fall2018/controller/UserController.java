package com.csye6255.web.application.fall2018.controller;

import com.csye6255.web.application.fall2018.dao.AttachmentDAO;
import com.csye6255.web.application.fall2018.dao.TransactionDAO;
import com.csye6255.web.application.fall2018.dao.UserDAO;
import com.csye6255.web.application.fall2018.pojo.Attachment;
import com.csye6255.web.application.fall2018.pojo.Transaction;
import com.csye6255.web.application.fall2018.pojo.User;
import com.csye6255.web.application.fall2018.utilities.AuthorizationUtility;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

@Controller
@Profile("dev")
public class UserController {

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserDAO userDao;

    @Autowired
    TransactionDAO transactionDAO;

    @Autowired
    AttachmentDAO attachmentDAO;


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
            Gson gson = new Gson();
            if (userList.size() != 0) {
                User user = userList.get(0);
                if (encoder.matches(password, user.getPassword())) {
                    List<Transaction> transactionList = transactionDAO.findByUserId(user.getId());
                    if (((List) transactionList).size() != 0) {
                        for (Transaction transaction : transactionList) {
                            JsonObject jsonObject1 = new JsonObject();
                            jsonObject1.addProperty("id", transaction.getTransactionid());
                            jsonObject1.addProperty("description", transaction.getDescription());
                            jsonObject1.addProperty("amount", transaction.getAmount());
                            jsonObject1.addProperty("date", transaction.getDate());
                            jsonObject1.addProperty("merchant", transaction.getMerchant());
                            jsonObject1.addProperty("category", transaction.getCategory());
                            List<Attachment> attachmentList = attachmentDAO.findByTransaction(transaction);
                            JsonObject attachmentObj = new JsonObject();

                            if (attachmentList.size() != 0) {
                                for (Attachment attachment : attachmentList) {
                                    attachmentObj.addProperty("id", attachment.getAttachmentid());
                                    attachmentObj.addProperty("url", attachment.getUrl());
                                }
                            }
                            JsonElement attachmentJsonElement = gson.toJsonTree(attachmentObj);
                            jsonObject1.add("attachments", attachmentJsonElement);
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
                        Attachment attachment = new Attachment();
                        t.setTransactionid(transaction.getTransactionid());
                        t.setDescription(transaction.getDescription());
                        t.setAmount(transaction.getAmount());
                        t.setDate(transaction.getDate());
                        t.setMerchant(transaction.getMerchant());
                        t.setCategory(transaction.getCategory());
                        t.setUser(user);
                        transactionDAO.save(t);
                        if (transaction.getAttachments() != null) {
                            attachment.setUrl(transaction.getAttachments().get(0).getUrl());
                            attachment.setTransaction(t);
                            attachmentDAO.save(attachment);
                        }
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
                            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                    } else
                        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
                } else
                    return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            } else
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } else
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);

    }

    @RequestMapping(value = "/transaction/{transactionid}", method = RequestMethod.PUT, produces = {"application/json"},
            consumes = "application/json", headers = {"content-type=application/json; charset=utf-8"})
    @ResponseBody
    public ResponseEntity updateTransaction(@PathVariable("transactionid") String transactionid, HttpServletRequest request, @RequestBody Transaction transaction) {
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
                            trans.setTransactionid(transactionid);
                            trans.setAmount(transaction.getAmount());
                            trans.setCategory(transaction.getCategory());
                            trans.setDate(transaction.getDate());
                            trans.setDescription(transaction.getDescription());
                            trans.setMerchant(transaction.getMerchant());
                            transactionDAO.save(trans);
                            JsonObject jsonObject1 = new JsonObject();
                            jsonObject1.addProperty("id", trans.getTransactionid());
                            jsonObject1.addProperty("description", trans.getDescription());
                            jsonObject1.addProperty("merchant", trans.getMerchant());
                            jsonObject1.addProperty("amount", trans.getAmount());
                            jsonObject1.addProperty("date", trans.getDate());
                            jsonObject1.addProperty("category", trans.getCategory());
                            return ResponseEntity.status(HttpStatus.CREATED).body(jsonObject1.toString());
                        } else
                            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                    } else
                        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
                } else
                    return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            } else
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } else
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(value = "/transaction/{transactionid}/attachments", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseEntity attachFilesToTransaction(@PathVariable("transactionid") String transactionid,
                                                   HttpServletRequest request, @RequestParam("uploadReceipt") MultipartFile uploadReceiptFile) throws FileNotFoundException, IOException {
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
                            if (uploadReceiptFile.isEmpty()) {
                                logger.error("attachFilesToTransaction Method : No file to upload");
                            }
                            String path = System.getProperty("user.dir") + "/images";        //Absolute Project Path
                            logger.info(path);
                            if (!uploadReceiptFile.isEmpty()) {
                                String filename = uploadReceiptFile.getOriginalFilename();
                                String suffix = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
                                String filePath = path + "/";
                                String newFileName = System.currentTimeMillis() + "." + suffix;
                                if (!suffix.equals("png") && !suffix.equals("jpg") && !suffix.equals("jpeg")) {
                                    String errMsg = "Please upload image file( supported type: *.png/*.jpeg/*.jpg )";
                                    logger.info(errMsg);
                                } else {
                                    try {
                                        logger.info(filePath + newFileName);
                                        uploadReceiptFile.transferTo(new File(filePath, newFileName));
                                        // Storing meta data in the DB: MSQL
                                        Attachment attachmentNew = new Attachment();
                                        attachmentNew.setUrl(filePath + newFileName);
                                        attachmentNew.setTransaction(trans);
                                        attachmentDAO.save(attachmentNew);
                                        jsonObject.addProperty("message", "File attached successfully");

                                        return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());

                                    } catch (Exception ex) {
                                        jsonObject.addProperty("message", "Error while storing in local storage " + ex.getMessage());
                                        logger.error("attachFilesToTransaction Method : exception" + ex.getMessage());

                                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonObject.toString());

                                    }
                                }
                            } else
                                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                        } else
                            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
                    } else {
                        jsonObject.addProperty("message", "No transaction found for the user");
                        return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
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
        return null;
    }

    @RequestMapping(value = "/transaction/{transactionid}/attachments/{attachmentid}", method = RequestMethod.PUT, produces = {"application/json"})
    @ResponseBody
    public ResponseEntity replaceAttachment(@PathVariable("transactionid") String transactionid,
                                            @PathVariable("attachmentid") String attachmentid, HttpServletRequest request,
                                            @RequestParam("uploadReceipt") MultipartFile uploadReceiptFile) throws FileNotFoundException, IOException {
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
                            if (uploadReceiptFile.isEmpty()) {
                                logger.error("attachFilesToTransaction Method : No file to upload");
                            }
                            String path = System.getProperty("user.dir") + "/images";        //Absolute Project Path
                            logger.info(path);
                            if (!uploadReceiptFile.isEmpty()) {
                                String filename = uploadReceiptFile.getOriginalFilename();
                                String suffix = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
                                String filePath = path + "/";
                                String newFileName = System.currentTimeMillis() + "." + suffix;
                                if (!suffix.equals("png") && !suffix.equals("jpg") && !suffix.equals("jpeg")) {
                                    String errMsg = "Please upload image file( supported type: *.png/*.jpeg/*.jpg )";
                                    logger.info(errMsg);
                                } else {
                                    try {

                                        List<Attachment> attachmentList = attachmentDAO.findByTransaction(trans);
                                        for (Attachment attachment1 : attachmentList) {
                                            if (attachment1.getAttachmentid().equals(attachmentid)) {
                                                File file = new File(attachment1.getUrl());
                                                if (file.exists()) {
                                                    file.delete();
                                                }
                                                logger.info(filePath + newFileName);
                                                uploadReceiptFile.transferTo(new File(filePath, newFileName));
                                                // Storing meta data in the DB: MSQL
                                                attachment1.setUrl(filePath + newFileName);
                                                attachmentDAO.save(attachment1);
                                                jsonObject.addProperty("message", "File updated successfully");

                                                return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
                                            }
                                        }

                                    } catch (Exception ex) {
                                        jsonObject.addProperty("message", "Error while storing in local storage " + ex.getMessage());
                                        logger.error("attachFilesToTransaction Method : exception" + ex.getMessage());

                                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonObject.toString());

                                    }
                                }
                            } else
                                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                        } else
                            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
                    } else {
                        jsonObject.addProperty("message", "No transaction found for the user");
                        return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
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
        jsonObject.addProperty("message", "Attachment Not Found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(jsonObject.toString());
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
                    List<Transaction> transactionList = transactionDAO.findByTransactionid(transactionid);
                    if (transactionList.size() != 0) {
                        Transaction trans = transactionList.get(0);
                        if (trans.getUser().getId() == user.getId()) {
                            List<Attachment> attachmentList = attachmentDAO.findByTransaction(trans);
                            if (attachmentList.size() != 0) {
                                for (Attachment attachment : attachmentList) {
                                    JsonObject attachmentObj = new JsonObject();
                                    attachmentObj.addProperty("id", attachment.getAttachmentid());
                                    attachmentObj.addProperty("url", attachment.getUrl());
                                    jsonObjectList.add(attachmentObj);
                                }
                                return ResponseEntity.status(HttpStatus.OK).body(jsonObjectList.toString());
                            } else {
                                jsonObject.addProperty("message", "There are no attachments on this transaction");
                                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(jsonObject.toString());
                            }
                        } else {
                            jsonObject.addProperty("message", "User not found! - Try Logging in again");
                            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
                        }
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

    @RequestMapping(value = "/transaction/{transactionid}/attachments/{attachmentid}", method = RequestMethod.DELETE, produces = {"application/json"})
    @ResponseBody
    public ResponseEntity deleteAttachment(@PathVariable("transactionid") String transactionid,
                                           @PathVariable("attachmentid") String attachmentid, HttpServletRequest request) throws FileNotFoundException, IOException {
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
                            List<Attachment> attachmentList = attachmentDAO.findAttachmentByAttachmentid(attachmentid);
                            if (attachmentList.size() != 0) {
                                Attachment attachment = attachmentList.get(0);
                                try {
                                    File file = new File(attachment.getUrl());
                                    if (file.exists()) {
                                        file.delete();
                                        attachmentDAO.delete(attachment);
                                        jsonObject.addProperty("message", "File deleted successfully");
                                        return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
                                    } else {
                                        logger.error("deleteAttachment Method : No file to delete");
                                    }
                                } catch (Exception ex) {
                                    jsonObject.addProperty("message", "Error while storing in local storage " + ex.getMessage());
                                    logger.error("deleteAttachment Method : exception" + ex.getMessage());

                                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonObject.toString());
                                }

                            } else {
                                jsonObject.addProperty("message", "No attachments found to delete");
                                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
                            }
                        } else {
                            jsonObject.addProperty("message", "User not found! - Try Logging in again");
                            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
                        }
                    } else {
                        jsonObject.addProperty("message", "No transaction found for the user");
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
            jsonObject.addProperty("message", "You are not logged in - Provide Username and Password!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(jsonObject.toString());
        }
        jsonObject.addProperty("message", "Attachment Not Found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(jsonObject.toString());
    }
}
