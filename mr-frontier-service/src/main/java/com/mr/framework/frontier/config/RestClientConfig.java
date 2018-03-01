package com.mr.framework.frontier.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {  
  
    @Bean
	public RestTemplate restTemplate() {
        return new RestTemplate(clientHttpRequestFactory());  
    }  
  
    private ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(2000);  
        factory.setConnectTimeout(2000);  
        return factory;  
    }  
}  