package com.sat.tracker.dto;

import com.sat.tracker.model.Certificate;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CertificateDto {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentIdCode;
    private String title;
    private String description;
    private String eventType;
    private String achievementType;
    private String fileType;
    private LocalDateTime uploadDate;
    private String staffStatus;
    private String staffFeedback;
    private LocalDateTime staffReviewDate;
    private String hodStatus;
    private String hodFeedback;
    private LocalDateTime hodReviewDate;

    public static CertificateDto from(Certificate c) {
        return CertificateDto.builder()
                .id(c.getId())
                .studentId(c.getStudent().getId())
                .studentName(c.getStudent().getName())
                .studentIdCode(c.getStudent().getStudentId())
                .title(c.getTitle())
                .description(c.getDescription())
                .eventType(c.getEventType())
                .achievementType(c.getAchievementType())
                .fileType(c.getFileType())
                .uploadDate(c.getUploadDate())
                .staffStatus(c.getStaffStatus().name().toLowerCase())
                .staffFeedback(c.getStaffFeedback())
                .staffReviewDate(c.getStaffReviewDate())
                .hodStatus(c.getHodStatus().name().toLowerCase())
                .hodFeedback(c.getHodFeedback())
                .hodReviewDate(c.getHodReviewDate())
                .build();
    }
}
