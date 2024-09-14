package com.plp.course_service.storage;
import com.plp.course_service.model.Semester;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(
        name = "courses",
        schema = "course_service_repository"
)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CourseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long id;

    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Column(name = "faculty", nullable = false)
    private String faculty;

    @Enumerated(EnumType.STRING)
    @Column(name = "semester", nullable = false)
    private Semester semester;

    @Column(name = "ects", nullable = false)
    private Integer ects;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<CourseParticipantEntity> participants;
}
