package com.sat.tracker.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class StudentDashboardDto {
    private StudentProfileDto student;
    private List<CertificateDto> certificates;
    private List<NotificationDto> notifications;
    private Map<String, Long> stats;
}
