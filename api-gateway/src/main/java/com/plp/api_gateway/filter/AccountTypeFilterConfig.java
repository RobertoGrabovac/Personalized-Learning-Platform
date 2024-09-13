package com.plp.api_gateway.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient.Builder;

@Configuration
public class AccountTypeFilterConfig {

    private final Builder webClientBuilder;

    public AccountTypeFilterConfig(Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Bean
    public AccountTypeFilter adminAccountTypeCheckFilter() {
        return new AccountTypeFilter(webClientBuilder, new AccountTypeFilter.Config(AccountType.ADMIN));
    }
}
