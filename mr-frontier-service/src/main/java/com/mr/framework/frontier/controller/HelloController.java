package com.mr.framework.frontier.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by feng on 2018/2/13.
 */
@RestController
public class HelloController {

    @RequestMapping("/say")
    public Object hello() {

        return "Hello, This is Restful api";
    }

}
