package com.sat.tracker.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentStatDto {
    private Long studentId;
    private String name;
    private String section;
    private Integer year;
    private long certificateCount;
    private long winningCount;
    private long participationCount;
}
