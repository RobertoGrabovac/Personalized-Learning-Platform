package com.plp.learning_recommender_service.model;

import com.plp.learning_recommender_service.config.Azure;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
@ConfigurationProperties(prefix = "learning-recommender-service")
public class LearningRecommender {

    @NotNull
    @Valid
    private Azure azure;

    public Azure getAzure() {
        return azure;
    }

    public void setAzure(Azure azure) {
        this.azure = azure;
    }
}
