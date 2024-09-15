package com.plp.course_service.controller;

import com.plp.course_service.model.CourseResponseDTO;
import com.plp.course_service.service.CourseService;
import com.plp.course_service.storage.CourseEntity;
import com.plp.course_service.storage.CourseParticipantEntity;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<List<CourseResponseDTO>> getAllCourses() {
        List<CourseResponseDTO> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponseDTO> getCourse(@PathVariable Long courseId) {
        CourseResponseDTO courseResponse = courseService.getCourseResponse(courseId);
        return ResponseEntity.ok(courseResponse);
    }

    @PostMapping
    public ResponseEntity<CourseEntity> createCourse(@RequestBody CourseEntity courseEntity) {
        CourseEntity createdCourse = courseService.createCourse(courseEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
    }

    @PostMapping("/{courseId}/enroll")
    public ResponseEntity<String> enrollInCourse(
            @PathVariable Long courseId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String username = extractUsernameFromAuthHeader(authHeader);
            Long userId = getUserIdByUsername(username);

            courseService.enrollUserInCourse(userId, courseId);
            return ResponseEntity.ok("User enrolled successfully in course with ID: " + courseId);
        } catch (EntityNotFoundException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{courseId}/participants")
    public ResponseEntity<Set<CourseParticipantEntity>> getParticipantsForCourse(@PathVariable Long courseId) {
        Set<CourseParticipantEntity> participants = courseService.getParticipantsForCourse(courseId);
        return ResponseEntity.ok(participants);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long courseId) {
        try {
            courseService.deleteCourse(courseId);
            return ResponseEntity.ok("Course deleted successfully with ID: " + courseId);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{courseId}/participants/{userId}")
    public ResponseEntity<String> removeParticipant(
            @PathVariable Long courseId,
            @PathVariable Long userId) {
        try {
            courseService.removeParticipantFromCourse(userId, courseId);
            return ResponseEntity.ok("User with ID " + userId + " removed from course " + courseId);
        } catch (EntityNotFoundException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    private String extractUsernameFromAuthHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            String base64Credentials = authHeader.substring("Basic".length()).trim();
            byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(decodedBytes);
            String[] values = credentials.split(":", 2);
            return values[0];
        }
        throw new IllegalArgumentException("Invalid Authorization header");
    }

    // TODO: refactor
    private Long getUserIdByUsername(String username) {
        String url = "http://user-service:8081/users/username/" + username;
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, Long.class);
    }
}
