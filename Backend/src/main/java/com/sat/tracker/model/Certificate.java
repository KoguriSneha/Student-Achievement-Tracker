package com.sat.tracker.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;    

@Entity
@Table(name = "certificate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    /** workshop, hackathon, technical, cultural */
    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    /** winning, participation */
    @Column(name = "achievement_type", nullable = false, length = 50)
    private String achievementType;

    @Column(name = "file_path", nullable = false, length = 255)
    private String filePath;

    /** pdf, jpg, jpeg */
    @Column(name = "file_type", nullable = false, length = 10)
    private String fileType;

    @Column(name = "upload_date")
    @Builder.Default
    private LocalDateTime uploadDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "staff_status", nullable = false, length = 20)
    @Builder.Default
    private ReviewStatus staffStatus = ReviewStatus.PENDING;

    @Column(name = "staff_feedback", columnDefinition = "TEXT")
    private String staffFeedback;

    @Column(name = "staff_review_date")
    private LocalDateTime staffReviewDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "hod_status", nullable = false, length = 20)
    @Builder.Default
    private ReviewStatus hodStatus = ReviewStatus.PENDING;

    @Column(name = "hod_feedback", columnDefinition = "TEXT")
    private String hodFeedback;

    @Column(name = "hod_review_date")
    private LocalDateTime hodReviewDate;
}
