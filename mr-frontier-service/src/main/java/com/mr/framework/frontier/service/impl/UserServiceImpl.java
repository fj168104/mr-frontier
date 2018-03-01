package com.mr.framework.frontier.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.mr.framework.frontier.domain.User;
import com.mr.framework.frontier.service.UserService;

/**
 * Created by feng on 2018/2/13.
 */
@Service
public class UserServiceImpl implements UserService {

    public User saveUser(User user) {
        user.setId(1);
        System.out.println(user.toString());
        return user;
    }
}
