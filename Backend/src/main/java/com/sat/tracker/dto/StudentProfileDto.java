package com.sat.tracker.dto;

import com.sat.tracker.model.Student;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentProfileDto {
    private Long id;
    private String name;
    private String studentId;
    private String branch;
    private String section;
    private Integer year;
    private String classTeacherName;
    private String email;
    private String username;

    public static StudentProfileDto from(Student s) {
        return StudentProfileDto.builder()
                .id(s.getId())
                .name(s.getName())
                .studentId(s.getStudentId())
                .branch(s.getBranch())
                .section(s.getSection())
                .year(s.getYear())
                .classTeacherName(s.getClassTeacher() != null ? s.getClassTeacher().getName() : null)
                .email(s.getUser().getEmail())
                .username(s.getUser().getUsername())
                .build();
    }
}
