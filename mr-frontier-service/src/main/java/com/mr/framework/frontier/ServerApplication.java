package com.mr.framework.frontier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot 应用启动类
 * <p>
 * Created by feng on 13/02/2018.
 */
// Spring Boot 应用的标识
@SpringBootApplication
@EnableZuulProxy
public class ServerApplication {

	public static void main(String[] args) {
		// 程序启动入口
		// 启动嵌入式的 Tomcat 并初始化 Spring 环境及其各 Spring 组件
		SpringApplication.run(ServerApplication.class, args);
	}

	@Bean
	public PreCommonFilter preCommonFilter() {
		return new PreCommonFilter();
	}

	@Bean
	public PostCommonFilter postCommonFilter() {
		return new PostCommonFilter();
	}
}
