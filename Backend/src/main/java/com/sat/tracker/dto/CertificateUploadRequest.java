package com.sat.tracker.dto;

import lombok.Data;

@Data
public class CertificateUploadRequest {
    private String title;
    private String description;
    private String eventType;
    private String achievementType;
}
