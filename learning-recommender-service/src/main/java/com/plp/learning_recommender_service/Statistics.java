package com.plp.learning_recommender_service;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class Statistics {

    private static final String RECOMMEND_COURSE_COUNTER_NAME = "recommend_course_count";
    private static final String ANALYZE_COURSE_COUNTER_NAME = "analyze_course_count";

    private final MeterRegistry meterRegistry;

    public Statistics(MeterRegistry meterRegistry) {this.meterRegistry = meterRegistry;}

    public void registerReccomendCourse() {
        meterRegistry.counter(RECOMMEND_COURSE_COUNTER_NAME).increment();
    }

    public void registerAnalyzeCourse() {
        meterRegistry.counter(ANALYZE_COURSE_COUNTER_NAME).increment();
    }
}
