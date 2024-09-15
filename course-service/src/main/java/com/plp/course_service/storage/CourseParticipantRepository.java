package com.plp.course_service.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseParticipantRepository extends JpaRepository<CourseParticipantEntity, Long> {

    boolean existsByUserIdAndCourse(Long userId, CourseEntity course);

    Optional<CourseParticipantEntity> findByUserIdAndCourseId(Long userId, Long courseId);
}
