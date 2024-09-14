package com.plp.api_gateway.filter;

import com.plp.api_gateway.Statistics;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.NoSuchElementException;

public class AccountTypeFilter extends AbstractGatewayFilterFactory<AccountTypeFilter.Config> implements Ordered {

    private final WebClient webClient;
    private final Config config;
    private final Statistics statistics;

    private static final Map<String, String> PATH_TO_SERVICE_MAP = Map.of(
            "/users", "user-service",
            "/course", "learning-recommender-service"
    );

    public AccountTypeFilter(Builder webClientBuilder, Config config, Statistics statistics) {
        super(Config.class);
        this.webClient = webClientBuilder.baseUrl("http://user-service:8081").build();
        this.config = config;
        this.statistics = statistics;
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

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            String path = exchange.getRequest().getURI().getPath();
            String serviceName = PATH_TO_SERVICE_MAP.entrySet().stream()
                    .filter(entry -> path.startsWith(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse("unknown-service");

            statistics.incrementServiceCalls(serviceName);

            if (authHeader == null || authHeader.isEmpty()) {
                return createErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Missing or empty Authorization header");
            }

            return webClient.get()
                    .uri("/users/validateAccountType?accountType=" + config.getAccountType().name())
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, this::handleClientError)
                    .bodyToMono(Boolean.class)
                    .flatMap(hasRole -> Boolean.TRUE.equals(hasRole)
                            ? chain.filter(exchange)
                            : createErrorResponse(exchange, HttpStatus.FORBIDDEN,
                            "Forbidden: User does not have the required account type"))
                    .onErrorResume(e -> handleError(e, exchange));
        };
    }

    // TODO: ask if there is more convenient way of resolving errors via api-gateway (handleClientError&handleError)
    private Mono<Throwable> handleClientError(ClientResponse response) {
        HttpStatus status = (HttpStatus) response.statusCode();

        String statusCode = switch (status) {
            case UNAUTHORIZED -> "UNAUTHORIZED";
            case NOT_FOUND -> "NOT_FOUND";
            case BAD_REQUEST -> "BAD_REQUEST";
            default -> "INTERNAL_SERVER_ERROR";
        };

        statistics.incrementHttpsStatusCounter(statusCode);

        return Mono.error(switch (status) {
            case UNAUTHORIZED -> new SecurityException("Invalid API key");
            case NOT_FOUND -> new NoSuchElementException("User not found");
            default -> new IllegalArgumentException("Invalid input");
        });
    }

    private Mono<Void> handleError(Throwable e, ServerWebExchange exchange) {
        return Mono.defer(() -> {
            HttpStatus status;
            String message;

            switch (e) {
                case SecurityException se -> {
                    status = HttpStatus.UNAUTHORIZED;
                    message = se.getMessage();
                }
                case NoSuchElementException nsee -> {
                    status = HttpStatus.NOT_FOUND;
                    message = nsee.getMessage();
                }
                case IllegalArgumentException iae -> {
                    status = HttpStatus.BAD_REQUEST;
                    message = iae.getMessage();
                }
                default -> {
                    status = HttpStatus.INTERNAL_SERVER_ERROR;
                    message = "An internal server error occurred";
                }
            }

            return createErrorResponse(exchange, status, message);
        });
    }

    private Mono<Void> createErrorResponse(ServerWebExchange exchange, HttpStatus status, String message) {
        exchange.getResponse().setStatusCode(status);
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
