package com.ljheee.page.controller;

import com.ljheee.page.dao.UserDAO;
import com.ljheee.page.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by lijianhua04 on 2018/7/20.
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserDAO userDAO;


    @GetMapping("/query")
    public List<User> getUsersByPage(){

        return userDAO.getUserList();
    }

    @GetMapping("/alive")
    public String alive(){
        return "ok";
    }


}
