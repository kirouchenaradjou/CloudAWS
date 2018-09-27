package com.csye6255.web.application.fall2018.controller;

import com.csye6255.web.application.fall2018.dao.UserDAO;
import com.csye6255.web.application.fall2018.pojo.User;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Controller
public class UserController {

    @Autowired
    UserDAO userDao;


//    @RequestMapping(value="/" , method = RequestMethod.GET)
//    public String index(HttpServletRequest request) {
//        HttpSession session = (HttpSession) request.getSession();
//
//        if(session.getAttribute("alreadyLoggedInSession")!= null)
//        {
//            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//            Date date = new Date();
//            System.out.println(dateFormat.format(date));
//            return "Current System Date is " + date;
//        }
//
//        else
//            return "You are not logged ib";
//    }

//    @RequestMapping(value = "/login", method = RequestMethod.POST)
//    public String login(HttpServletRequest request) {
//
//        HttpSession session = (HttpSession) request.getSession();
//
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//
//        System.out.print(request.getParameter("userName"));
//        List<User> userList = userDao.findByUserName(request.getParameter("userName"));
//
//        if (userList.size() != 0) {
//            User user = userList.get(0);
//
//            if (encoder.matches(request.getParameter("password"), user.getPassword())) {
//                session.setAttribute("puser",user.getUserName());
//                session.setAttribute("alreadyLoggedInSession","true");
//                session.setAttribute("currentUser", user);
//                return "register.html";
//            } else
//                return "haha.html";
//        }
//
//        return "haha.html";
//    }
//
//    @RequestMapping(value = "/user/register", method = RequestMethod.POST)
//    public String userregister(HttpServletRequest request) {
//
//        HttpSession session = (HttpSession) request.getSession();
//
//
//        List<User> userList = userDao.findByUserName(request.getParameter("userName"));
//
//        if (userList.size() == 0) {
//            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//            String password = request.getParameter("password");
//            String hashedpassword = passwordEncoder.encode(password);
//
//            User user = new User();
//            user.setUserName(request.getParameter("userName"));
//            user.setPassword(hashedpassword);
//            userDao.save(user);
//
//            return "Account Created Successfully";
//
//        } else
//            return "Account Already Exists";
//    }
//    @RequestMapping(value = "/time", method = RequestMethod.GET)
//    public String getCurrentTime(HttpServletRequest request) {
//
//        return "registeredUser" ;
//    }
//

    @RequestMapping(value = "/user/register", method = RequestMethod.POST, produces = {"application/json"},
            consumes = "application/json", headers = {"content-type=application/json; charset=utf-8"})
    @ResponseBody
    public String postRegister(@RequestBody User user) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        JsonObject jsonObject = new JsonObject();


        List<User> userList = userDao.findByUserName(user.getUserName());

        if (userList.size() == 0) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String password = user.getPassword();
            String hashedPassword = passwordEncoder.encode(password);

            User u = new User();
            u.setUserName(user.getUserName());
            u.setPassword(hashedPassword);
            userDao.save(u);
            jsonObject.addProperty("message", "Registration Successful");


        } else jsonObject.addProperty("message", "User already exists!");

        return jsonObject.toString();


    }

    @RequestMapping(value = "/dontchangethis", method = RequestMethod.POST, produces = {"application/json"},
            consumes = "application/json", headers = {"content-type=application/json; charset=utf-8"})
    @ResponseBody
    public String postLogin(@RequestBody User user) {



        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        JsonObject jsonObject = new JsonObject();


        List<User> userList = userDao.findByUserName(user.getUserName());

        if (userList.size() != 0) {
            User foundUser = userList.get(0);

            System.out.println("User was found!! His username is " + foundUser.getUserName()
                    + " and his password is "
                    + foundUser.getPassword());

            if (encoder.matches(user.getPassword(), foundUser.getPassword())) {
                System.out.println("Size of the list is =" + userList.size());

                jsonObject.addProperty("message", "success");
                jsonObject.addProperty("time", new Date().toString());
            } else jsonObject.addProperty("message", "Incorrect Password");


        } else jsonObject.addProperty("message", "User not found!");

        return jsonObject.toString();


    }
    @RequestMapping(value = "/time", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String checkSession(@RequestHeader HttpHeaders headers)
    {
        JsonObject jsonObject = new JsonObject();
        if(headers !=null && headers.get("Authorization") !=null)
            jsonObject.addProperty("message","Current Time is "+ new Date().toString());

        else
            jsonObject.addProperty("message","You are not logged in!");


        return jsonObject.toString();
    }

}