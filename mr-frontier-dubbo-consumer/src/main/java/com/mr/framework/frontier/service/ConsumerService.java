package com.mr.framework.frontier.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.mr.framework.frontier.domain.User;
import org.springframework.stereotype.Component;

/**
 * 城市 Dubbo 服务消费者
 * <p>
 * Created by feng on 13/02/2018.
 */
@Component
public class ConsumerService {

    @Reference
    UserService dubboService;

    public User saveUser() {
        User user = new User();
        user.setUsername("jfeng");
        user.setPassword("feng888");
        return dubboService.saveUser(user);
    }

}
