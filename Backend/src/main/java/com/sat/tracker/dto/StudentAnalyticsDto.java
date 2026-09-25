package com.sat.tracker.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class StudentAnalyticsDto {
    private long totalCertificates;
    private Map<String, Long> eventTypeData;
    private Map<String, Long> achievementData;
    private Map<String, Long> statusData;
}
