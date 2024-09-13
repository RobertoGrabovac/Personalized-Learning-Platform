package com.plp.api_gateway;

import com.plp.api_gateway.filter.AccountTypeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutingConfig {

    @Autowired
    private AccountTypeFilter adminAccountTypeFilter;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {

        return builder.routes()
                .route(p -> p
                        .path("/users/**")
                        .filters(f -> f.filter(adminAccountTypeFilter.apply(adminAccountTypeFilter.getConfig())))
                        .uri("http://user-service:8081")
                ).build();
    }
}
