package com.sat.tracker.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HodDashboardDto {
    private HodProfileDto hod;
    private long pendingCount;
    private List<NotificationDto> notifications;
    private BranchAnalyticsDto analytics;
}
