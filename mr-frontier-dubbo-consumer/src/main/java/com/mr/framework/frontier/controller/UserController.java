package com.mr.framework.frontier.controller;

import com.mr.framework.frontier.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by feng on 2018/2/13.
 */
@RestController
public class UserController {

    @Autowired
    private ConsumerService service;


    @RequestMapping("/save")
    public Object saveUser() {

        return service.saveUser();
    }

}
