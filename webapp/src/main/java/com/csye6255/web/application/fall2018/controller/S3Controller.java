package com.csye6255.web.application.fall2018.controller;

import com.csye6255.web.application.fall2018.dao.AttachmentDAO;
import com.csye6255.web.application.fall2018.dao.TransactionDAO;
import com.csye6255.web.application.fall2018.dao.UserDAO;
import com.csye6255.web.application.fall2018.pojo.Attachment;
import com.csye6255.web.application.fall2018.pojo.Transaction;
import com.csye6255.web.application.fall2018.pojo.User;
import com.csye6255.web.application.fall2018.utilities.AuthorizationUtility;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
                                                   HttpServletRequest request, Attachment attachment) throws FileNotFoundException, IOException {
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
                            if (attachment.getUrl()== null) {
                                logger.error("attachFilesToTransaction Method : No file to upload");
                            }
                            String path = System.getProperty("user.dir") + "/images";		//Absolute Project Path
                            logger.info(path);
                            if (!attachment.getUrl().isEmpty()) {

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
        }
        else {
            jsonObject.addProperty("message", "You are not logged in - Provide Username and Password!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(jsonObject.toString());
        }
        return null;
    }

}
