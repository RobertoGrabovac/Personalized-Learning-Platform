package com.plp.api_gateway;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class Statistics {

    private static final String USER_SERVICE_CALLS_COUNTER_NAME = "user_service_calls_count";
    private static final String LEARNING_RECOMMENDER_SERVICE_CALLS_COUNTER_NAME = "learning_recommender_service_calls_count";

    private static final String UNAUTHORIZED_COUNTER_NAME = "http_unauthorized_count";
    private static final String NOT_FOUND_COUNTER_NAME = "http_not_found_count";
    private static final String BAD_REQUEST_COUNTER_NAME = "http_bad_request_count";
    private static final String INTERNAL_SERVER_ERROR_COUNTER_NAME = "http_internal_server_error_count";

    private final MeterRegistry meterRegistry;

    public Statistics(MeterRegistry meterRegistry) {this.meterRegistry = meterRegistry;}

    public void incrementServiceCalls(String serviceName) {
        String counterName = switch (serviceName) {
            case "user-service" -> USER_SERVICE_CALLS_COUNTER_NAME;
            case "learning-recommender-service" -> LEARNING_RECOMMENDER_SERVICE_CALLS_COUNTER_NAME;
            default -> throw new IllegalArgumentException("Unknown service: " + serviceName);
        };
        meterRegistry.counter(counterName).increment();
    }

    public void incrementHttpsStatusCounter(String statusCode) {
        switch (statusCode) {
            case "UNAUTHORIZED":
                meterRegistry.counter(UNAUTHORIZED_COUNTER_NAME).increment();
                break;
            case "NOT_FOUND":
                meterRegistry.counter(NOT_FOUND_COUNTER_NAME).increment();
                break;
            case "BAD_REQUEST":
                meterRegistry.counter(BAD_REQUEST_COUNTER_NAME).increment();
                break;
            case "INTERNAL_SERVER_ERROR":
                meterRegistry.counter(INTERNAL_SERVER_ERROR_COUNTER_NAME).increment();
                break;
            default:
                throw new IllegalArgumentException("Unknown status code: " + statusCode);
        }
    }
}
