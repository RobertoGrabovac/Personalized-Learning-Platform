package com.plp.course_service.controller;

import com.plp.course_service.model.CourseResponseDTO;
import com.plp.course_service.service.CourseService;
import com.plp.course_service.storage.CourseEntity;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @RequestParam Long userId) {
        try {
            courseService.enrollUserInCourse(userId, courseId);
            return ResponseEntity.ok("User enrolled successfully in course with ID: " + courseId);
        } catch (EntityNotFoundException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //    @GetMapping("/{courseId}/participants")
    //    public ResponseEntity<Set<CourseParticipantEntity>> getParticipantsForCourse(@PathVariable Long courseId) {
    //        Set<CourseParticipantEntity> participants = courseService.getParticipantsForCourse(courseId);
    //        return ResponseEntity.ok(participants);
    //    }
}
