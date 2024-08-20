package com.eszczesniak.github_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class AppConfig {

    private static final String GITHUB_TOKEN = "";  // Replace with your token

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        ClientHttpRequestInterceptor authInterceptor = (request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();
            headers.add("Authorization", "Bearer " + GITHUB_TOKEN);
            return execution.execute(request, body);
        };

        restTemplate.setInterceptors(Collections.singletonList(authInterceptor));
        return restTemplate;
    }
}