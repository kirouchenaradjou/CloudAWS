package com.csye6255.web.application.fall2018.controller;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.iterable.S3Objects;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.csye6255.web.application.fall2018.dao.AttachmentDAO;
import com.csye6255.web.application.fall2018.dao.TransactionDAO;
import com.csye6255.web.application.fall2018.dao.UserDAO;
import com.csye6255.web.application.fall2018.pojo.Attachment;
import com.csye6255.web.application.fall2018.pojo.Transaction;
import com.csye6255.web.application.fall2018.pojo.User;
import com.csye6255.web.application.fall2018.utilities.AuthorizationUtility;
import com.csye6255.web.application.fall2018.utilities.S3BucketUtility;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author rkirouchenaradjou
 */
@Controller
@Profile("aws")
public class S3Controller {

    private final static Logger logger = LoggerFactory.getLogger(S3Controller.class);

    @Autowired
    UserDAO userDao;

    @Autowired
    TransactionDAO transactionDAO;

    @Autowired
    AttachmentDAO attachmentDAO;

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
                                jsonObject.addProperty("message", "No file to upload");
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonObject.toString());
                            }
                            if (!uploadReceiptFile.isEmpty()) {
                                String filename = uploadReceiptFile.getOriginalFilename();
                                String suffix = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
                                String newFileName = System.currentTimeMillis() + "." + suffix;
                                if (!suffix.equals("png") && !suffix.equals("jpg") && !suffix.equals("jpeg")) {
                                    String errMsg = "Please upload image file( supported type: *.png/*.jpeg/*.jpg )";
                                    logger.info(errMsg);
                                    jsonObject.addProperty("message", errMsg);
                                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonObject.toString());

                                } else {
                                    AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();
                                    try {
                                        String path = System.getProperty("user.dir") + "/images";
                                        String filePath = path + "/";
                                        InputStream is = uploadReceiptFile.getInputStream();
                                        String bucketName = "haha.me.csye6225.com";
                                        s3.putObject(new PutObjectRequest(bucketName, newFileName, is, new ObjectMetadata()).withCannedAcl(CannedAccessControlList.PublicRead));
                                        String url=S3BucketUtility.productRetrieveFileFromS3("",newFileName,bucketName);
                                        // Storing meta data in the DB: MSQL
                                        Attachment attachmentNew = new Attachment();
                                        attachmentNew.setTransaction(trans);
                                        attachmentNew.setUrl(url);
                                        attachmentDAO.save(attachmentNew);
                                        jsonObject.addProperty("id", attachmentNew.getAttachmentid());
                                        jsonObject.addProperty("url", attachmentNew.getUrl());
                                        return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
                                    } catch (AmazonServiceException e) {
                                        System.err.println(e.getErrorMessage());
                                        jsonObject.addProperty("message", e.getErrorMessage());
                                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonObject.toString());
                                    }
                                }
                            } else {
                                jsonObject.addProperty("message", "Upload Recepiet file is empty!!!");
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonObject.toString());
                            }
                        } else {
                            jsonObject.addProperty("message", "No transaction found for the user");
                            return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
                        }
                    }else{
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
                String bucketName = "haha.me.csye6225.com";
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
                                    if (attachment.getUrl()!=null) {
                                        String[] value =attachment.getUrl().split("/"+bucketName);
                                        String[] keyValue = value[1].split("/");
                                        AmazonS3 s3client = AmazonS3ClientBuilder.standard().build();
                                        String toDelete = "";
                                        for(S3ObjectSummary summary: S3Objects.inBucket(s3client, bucketName)){
                                            String imageName = summary.getKey();
                                            int i = imageName.lastIndexOf('.');
                                            String ownerName = imageName.substring(0, i);
                                            if(ownerName.equals(keyValue[1])){
                                                toDelete = imageName;
                                                break;
                                            }
                                        }
                                        if(!toDelete.equals("")){
                                            s3client.deleteObject(bucketName, toDelete);
                                        }
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
