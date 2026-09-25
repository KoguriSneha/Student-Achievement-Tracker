package com.sat.tracker.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class BranchAnalyticsDto {
    private List<StudentStatDto> studentStats;
    private Map<String, Long> eventTypeData;
    private Map<String, Long> sectionPerformance;
    private List<StudentStatDto> topPerformers;
    private long totalStudents;
    private long totalCertificates;
    private long totalWinning;
    private long totalParticipation;
}
