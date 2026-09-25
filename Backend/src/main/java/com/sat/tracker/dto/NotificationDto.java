package com.sat.tracker.dto;

import com.sat.tracker.model.Notification;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationDto {
    private Long id;
    private String message;
    private LocalDateTime createdAt;
    private boolean read;
    private Long certificateId;

    public static NotificationDto from(Notification n) {
        return NotificationDto.builder()
                .id(n.getId())
                .message(n.getMessage())
                .createdAt(n.getCreatedAt())
                .read(n.isRead())
                .certificateId(n.getCertificate() != null ? n.getCertificate().getId() : null)
                .build();
    }
}
