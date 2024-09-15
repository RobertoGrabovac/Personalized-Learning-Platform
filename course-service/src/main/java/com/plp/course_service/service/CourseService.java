package com.plp.course_service.service;

import com.plp.course_service.model.CourseResponseDTO;
import com.plp.course_service.storage.CourseEntity;
import com.plp.course_service.storage.CourseParticipantEntity;
import com.plp.course_service.storage.CourseParticipantRepository;
import com.plp.course_service.storage.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseParticipantRepository courseParticipantRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository, CourseParticipantRepository courseParticipantRepository) {
        this.courseRepository = courseRepository;
        this.courseParticipantRepository = courseParticipantRepository;
    }

    public CourseEntity getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + courseId));
    }

    public void enrollUserInCourse(Long userId, Long courseId) {
        CourseEntity course = getCourseById(courseId);

        if (courseParticipantRepository.existsByUserIdAndCourse(userId, course)) {
            throw new IllegalStateException("User is already enrolled in this course");
        }

        CourseParticipantEntity participant = new CourseParticipantEntity();
        participant.setUserId(userId);
        participant.setCourse(course);

        courseParticipantRepository.save(participant);
    }

    public Set<CourseParticipantEntity> getParticipantsForCourse(Long courseId) {
        CourseEntity course = getCourseById(courseId);
        return course.getParticipants();
    }

    public CourseEntity createCourse(CourseEntity courseEntity) {
        return courseRepository.save(courseEntity);
    }

    public List<CourseResponseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToCourseResponseDTO)
                .collect(Collectors.toList());
    }

    public CourseResponseDTO getCourseResponse(Long courseId) {
        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + courseId));

        CourseResponseDTO dto = new CourseResponseDTO();
        dto.setId(course.getId());
        dto.setCourseName(course.getCourseName());
        dto.setFaculty(course.getFaculty());
        dto.setSemester(course.getSemester());
        dto.setEcts(course.getEcts());

        List<Long> participantIds = course.getParticipants().stream()
                .map(CourseParticipantEntity::getUserId)
                .collect(Collectors.toList());
        dto.setParticipantIds(participantIds);

        return dto;
    }

    public void deleteCourse(Long courseId) {
        Optional<CourseEntity> course = courseRepository.findById(courseId);
        if (course.isPresent()) {
            courseRepository.delete(course.get());
        } else {
            throw new EntityNotFoundException("Course with ID " + courseId + " not found");
        }
    }

    public void removeParticipantFromCourse(Long userId, Long courseId) {
        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course with ID " + courseId + " not found"));

        CourseParticipantEntity participant = courseParticipantRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found in course " + courseId));

        course.getParticipants().remove(participant);
        courseParticipantRepository.delete(participant);
    }

    private CourseResponseDTO mapToCourseResponseDTO(CourseEntity course) {
        CourseResponseDTO dto = new CourseResponseDTO();
        dto.setId(course.getId());
        dto.setCourseName(course.getCourseName());
        dto.setFaculty(course.getFaculty());
        dto.setSemester(course.getSemester());
        dto.setEcts(course.getEcts());
        dto.setParticipantIds(
                course.getParticipants().stream()
                        .map(CourseParticipantEntity::getUserId)
                        .collect(Collectors.toList())
        );
        return dto;
    }
}

