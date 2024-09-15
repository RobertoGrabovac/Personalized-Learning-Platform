package com.plp.learning_recommender_service.service;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.*;
import com.plp.learning_recommender_service.model.LearningRecommender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecommenderService {

    private final OpenAIClient chatClient;
    private final LearningRecommender learningRecommender;

    private static final String RECOMMENDATION_PROMPT = """
            You are an expert in educational courses. Provide a course recommendation based on the following details:
            - Topic: %s
            - Faculty: %s
            - Semester: %s
            - ECTS: %s
            Provide a well-informed recommendation for course based on these details.
            """;

    private static final String ANALYSIS_PROMPT = """
            You are an expert in educational course analysis. Based on the following details, provide a comprehensive
            analysis:
            - Course: %s
            - Faculty: %s
            - Additional details: %s
            Provide insights into course trends, suitability, and any other relevant analysis.
            """;

    public RecommenderService(LearningRecommender learningRecommender, OpenAIClient openAIClient) {
        this.learningRecommender = learningRecommender;
        this.chatClient = openAIClient;
    }

    public String recommendCourse(String topic, String faculty, String semester, Integer ects) {
        String prompt = String.format(
                RECOMMENDATION_PROMPT,
                topic,
                faculty,
                semester,
                Optional.ofNullable(ects).map(String::valueOf).orElse("any")
        );

        return getChatCompletion(prompt);
    }

    public String getCourseAnalysis(String course, String faculty, String details) {
        String prompt = String.format(
                ANALYSIS_PROMPT,
                course,
                faculty,
                Optional.ofNullable(details).orElse("No additional details provided")
        );

        return getChatCompletion(prompt);
    }

    private String getChatCompletion(String prompt) {
        ChatCompletions chatCompletions = chatClient.getChatCompletions(
                learningRecommender.getAzure().getDeploymentName(),
                new ChatCompletionsOptions(List.of(new ChatRequestSystemMessage(prompt)))
        );

        return chatCompletions.getChoices().stream()
                .map(ChatChoice::getMessage)
                .map(ChatResponseMessage::getContent)
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
