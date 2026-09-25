package com.sat.tracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterHodRequest {
    @NotBlank private String username;
    @NotBlank private String password;
    @NotBlank private String email;
    @NotBlank private String name;
    @NotBlank private String hodId;
    @NotBlank private String branch;
}
