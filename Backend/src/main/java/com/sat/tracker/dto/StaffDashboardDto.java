package com.sat.tracker.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StaffDashboardDto {
    private StaffProfileDto staff;
    private List<StudentProfileDto> students;
    private long pendingCount;
    private List<NotificationDto> notifications;
}
