package com.plp.learning_recommender_service.controller;

import com.plp.learning_recommender_service.service.RecommenderService;
import jakarta.validation.constraints.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/course")
public class RecommenderController {

    private final RecommenderService recommenderService;

    public RecommenderController(RecommenderService recommenderService) {this.recommenderService = recommenderService;}

    @GetMapping("/recommend")
    public String recommendCourse(
            @RequestParam
            @NotBlank(message = "Topic must not be blank")
            @Size(min = 3, max = 50, message = "Topic must be between 3 and 50 characters")
            String topic,

            @RequestParam
            @NotBlank(message = "Faculty must not be blank")
            @Pattern(regexp = "(?i)^[A-Za-z ]+$", message = "Faculty must only contain letters and spaces")
            @Size(min = 3, max = 30, message = "Faculty must be between 3 and 30 characters")
            String faculty,

            @RequestParam
            @NotBlank(message = "Semester must not be blank")
            @Pattern(regexp = "(?i)^(Winter|Summer)$", message = "Semester must be in the format 'Spring', or 'Summer'")
            String semester,

            @RequestParam(required = false)
            @Min(value = 1, message = "Minimum ECTS is 1")
            @Max(value = 5, message = "Maximum ECTS is 10")
            Integer ects
    ) {
        return recommenderService.recommendCourse(topic, faculty, semester, ects);
    }

    @GetMapping("/analysis")
    public String getCourseAnalysis(
            @RequestParam
            @NotBlank(message = "Course must not be blank")
            @Size(min = 3, max = 50, message = "Course must be between 3 and 50 characters")
            String course,

            @RequestParam
            @NotBlank(message = "Faculty must not be blank")
            @Pattern(regexp = "(?i)^[A-Za-z ]+$", message = "Faculty must only contain letters and spaces")
            @Size(min = 3, max = 30, message = "Faculty must be between 3 and 30 characters")
            String faculty,

            @RequestParam(required = false)
            @Size(max = 200, message = "Additional details must be less than 200 characters")
            String details
    ) {
        return recommenderService.getCourseAnalysis(course, faculty, details);
    }
}
