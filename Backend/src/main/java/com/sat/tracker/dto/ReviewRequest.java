package com.sat.tracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReviewRequest {
    @NotBlank
    private String status; // approved | rejected

    private String feedback;
}
