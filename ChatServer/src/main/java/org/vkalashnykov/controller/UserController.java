package org.vkalashnykov.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.vkalashnykov.model.User;
import org.vkalashnykov.service.spring.UserService;

import javax.naming.AuthenticationException;

/**
 * Created by vkalashnykov on 26.11.16.
 */
@Controller
@RequestMapping(value="/chat")
public class UserController {
    @Autowired
    private UserService userService;
    private final int port = 8080;
    private String implementation;




    @RequestMapping(value="start")
    public @ResponseBody String start(){
        return "start";
    }

    @RequestMapping(value="login" , method = RequestMethod.POST)
    public String login(String username, String password) throws AuthenticationException {
        User user=userService.findByUsername(username);
        if (user!=null && password.equals(user.getPassword())){
            return "redirect:/start";
        }
        else {
            throw new AuthenticationException("Username or password is incorrect");
        }
    }


    public String getImplementation() {
        return implementation;
    }

    public void setImplementation(String implementation) {
        this.implementation = implementation;
    }
}
