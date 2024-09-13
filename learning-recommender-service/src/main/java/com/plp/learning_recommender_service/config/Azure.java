package com.plp.learning_recommender_service.config;

import jakarta.validation.constraints.NotNull;

public class Azure {

    @NotNull
    private String apiEndpoint;

    @NotNull
    private String apiKey;

    @NotNull
    private String deploymentName;

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getDeploymentName() {
        return deploymentName;
    }

    public void setDeploymentName(String deploymentName) {
        this.deploymentName = deploymentName;
    }
}
