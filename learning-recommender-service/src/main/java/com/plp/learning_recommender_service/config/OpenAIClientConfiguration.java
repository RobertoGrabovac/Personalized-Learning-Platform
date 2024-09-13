package com.plp.learning_recommender_service.config;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.OpenAIServiceVersion;
import com.azure.core.credential.AzureKeyCredential;
import com.plp.learning_recommender_service.model.LearningRecommender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIClientConfiguration {

    @Bean
    public OpenAIClient openAIClient(LearningRecommender learningRecommender) {
        var openAIClientProperties = learningRecommender.getAzure();

        return new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(openAIClientProperties.getApiKey()))
                .endpoint(openAIClientProperties.getApiEndpoint())
                .serviceVersion(OpenAIServiceVersion.V2023_05_15)
                .buildClient();
    }
}
