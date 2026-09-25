package com.sat.tracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterStaffRequest {
    @NotBlank private String username;
    @NotBlank private String password;
    @NotBlank private String email;
    @NotBlank private String name;
    @NotBlank private String staffId;
    @NotBlank private String branch;
    @NotBlank private String section;
    private Integer year;
}
