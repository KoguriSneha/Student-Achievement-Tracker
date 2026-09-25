package com.sat.tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterStudentRequest {
    @NotBlank private String username;
    @NotBlank private String password;
    @NotBlank private String email;
    @NotBlank private String name;
    @NotBlank private String studentId;
    @NotBlank private String branch;
    @NotBlank private String section;
    @NotNull  private Integer year;
    private Long classTeacherId; // optional Staff.id
}
