package com.orderservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient productRestClient(
            @Value("${product-service.base-url}") String productServiceBaseUrl) {

        return RestClient.builder()
                .baseUrl(productServiceBaseUrl)
                .build();
    }

    @Bean
    public RestClient productOAuth2RestClient(
            OAuth2AuthorizedClientManager authorizedClientManager,
            @Value("${product-service.base-url}") String productServiceBaseUrl) {

        OAuth2ClientHttpRequestInterceptor oauth2Interceptor =
                new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);

        oauth2Interceptor.setClientRegistrationIdResolver(
                request -> "keycloak"
        );

        return RestClient.builder()
                .baseUrl(productServiceBaseUrl)
                .requestInterceptor(oauth2Interceptor)
                .build();
    }
}