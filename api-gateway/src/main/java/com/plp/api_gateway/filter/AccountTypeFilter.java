package com.plp.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class AccountTypeFilter extends AbstractGatewayFilterFactory<AccountTypeFilter.Config> implements Ordered {

    private final WebClient webClient;
    private final Config config;

    public AccountTypeFilter(Builder webClientBuilder, Config config) {
        super(Config.class);
        this.webClient = webClientBuilder.baseUrl("http://user-service:8081").build();
        this.config = config;
    }

    public static class Config {
        private AccountType accountType;

        public Config(AccountType accountType) {
            this.accountType = accountType;
        }

        public AccountType getAccountType() {
            return accountType;
        }

        public void setRequiredRole(AccountType accountType) {
            this.accountType = accountType;
        }
    }

    public Config getConfig(){
        return config;
    }

    // TODO: wrong api key
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || authHeader.isEmpty()) {
                return createErrorResponse(exchange, HttpStatus.UNAUTHORIZED);
            }

            return webClient.get()
                    .uri("/users/validateAccountType?accountType=" + config.getAccountType().name())
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .flatMap(hasRole -> Boolean.TRUE.equals(hasRole)
                            ? chain.filter(exchange)
                            : createErrorResponse(exchange, HttpStatus.FORBIDDEN))
                    .onErrorResume(e -> createErrorResponse(exchange, HttpStatus.INTERNAL_SERVER_ERROR));
        };
    }

    private Mono<Void> createErrorResponse(ServerWebExchange exchange, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
