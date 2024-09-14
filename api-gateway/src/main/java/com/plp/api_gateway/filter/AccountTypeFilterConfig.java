package com.plp.api_gateway.filter;

import com.plp.api_gateway.Statistics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient.Builder;

@Configuration
public class AccountTypeFilterConfig {

    private final Builder webClientBuilder;
    private final Statistics statistics;

    public AccountTypeFilterConfig(Builder webClientBuilder, Statistics statistics) {
        this.statistics = statistics;
        this.webClientBuilder = webClientBuilder;
    }

    @Bean
    public AccountTypeFilter standardAccountTypeCheckFilter() {
        return new AccountTypeFilter(webClientBuilder, new AccountTypeFilter.Config(AccountType.STANDARD), statistics);
    }

    @Bean
    public AccountTypeFilter premiumAccountTypeCheckFilter() {
        return new AccountTypeFilter(webClientBuilder, new AccountTypeFilter.Config(AccountType.PREMIUM), statistics);
    }

    @Bean
    public AccountTypeFilter adminAccountTypeCheckFilter() {
        return new AccountTypeFilter(webClientBuilder, new AccountTypeFilter.Config(AccountType.ADMIN), statistics);
    }

}
