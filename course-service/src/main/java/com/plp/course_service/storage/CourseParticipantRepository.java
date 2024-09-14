package com.plp.course_service.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseParticipantRepository extends JpaRepository<CourseParticipantEntity, Long> {
    boolean existsByUserIdAndCourse(Long userId, CourseEntity course);
}
