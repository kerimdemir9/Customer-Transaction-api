package com.customer.transaction;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfiguration {
    public static final String LOCALHOST = "http://localhost:";

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
