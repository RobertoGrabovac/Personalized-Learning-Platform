package com.plp.course_service.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CourseResponseDTO {

    private Long id;
    private String courseName;
    private String faculty;
    private Semester semester;
    private Integer ects;
    private List<Long> participantIds;
}
