package com.ecommerce.order.config;

import com.ecommerce.common.interceptor.CorrelationIdInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient inventoryRestClient(
            RestClient.Builder builder,
            @Value("${inventory.service.url}") String inventoryUrl) {

        return builder
                .baseUrl(inventoryUrl)
                .requestInterceptor(new CorrelationIdInterceptor())
                .build();
    }
}