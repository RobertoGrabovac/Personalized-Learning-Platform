package com.plp.api_gateway;

import com.plp.api_gateway.filter.AccountTypeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutingConfig {

    @Autowired
    @Qualifier("standardAccountTypeCheckFilter")
    private AccountTypeFilter standardAccountTypeFilter;

    @Autowired
    @Qualifier("premiumAccountTypeCheckFilter")
    private AccountTypeFilter premiumAccountTypeFilter;

    @Autowired
    @Qualifier("adminAccountTypeCheckFilter")
    private AccountTypeFilter adminAccountTypeFilter;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {

        return builder.routes()
                .route(p -> p
                        .path("/users/**")
                        .filters(f -> f.filter(adminAccountTypeFilter.apply(adminAccountTypeFilter.getConfig())))
                        .uri("http://user-service:8081")
                ).route(p -> p
                        .path("/course/analysis")
                        .filters(f -> f.filter(premiumAccountTypeFilter.apply(premiumAccountTypeFilter.getConfig())))
                        .uri("http://learning-recommender-service:8082")
                ).route(p -> p
                        .path("/course/recommend")
                        .filters(f -> f.filter(standardAccountTypeFilter.apply(standardAccountTypeFilter.getConfig())))
                        .uri("http://learning-recommender-service:8082")
                ).build();
    }
}
