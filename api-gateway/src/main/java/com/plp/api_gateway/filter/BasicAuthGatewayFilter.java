package com.plp.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class BasicAuthGatewayFilter extends AbstractGatewayFilterFactory<BasicAuthGatewayFilter.Config> {

    private static final String EMPTY_STR = "";

    private static final String COLON_STR = ":";

    private static final String BASIC_STR = "Basic ";


    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {

                String basicAuthValue = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                basicAuthValue = basicAuthValue != null ? basicAuthValue.replace(BASIC_STR, EMPTY_STR) : EMPTY_STR;

                basicAuthValue = new String(Base64.getDecoder().decode(basicAuthValue.getBytes()));
                String[] credentials = basicAuthValue.split(COLON_STR);
                if (credentials.length == 2) {
                    String userName = credentials[0];
                    String password = credentials[1];

                    //Check credentials with difference sources like database, LDAP, static files, etc
                    //For demonstration purpose, here we are validating credentials with hard code values.
                    if (userName.equals("test-user") && password.equals("test-pwd")) {
                        return chain.filter(exchange);
                    }
                }

            }

            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();

        };
    }


    @Override
    public Class<Config> getConfigClass() {
        return Config.class;
    }

    static class Config {

    }
}
