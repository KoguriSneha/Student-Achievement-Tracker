package com.sat.tracker.service;

import com.sat.tracker.dto.CertificateDto;
import com.sat.tracker.dto.CertificateUploadRequest;
import com.sat.tracker.dto.ReviewRequest;
import com.sat.tracker.exception.AccessDeniedCustomException;
import com.sat.tracker.exception.BadRequestException;
import com.sat.tracker.exception.ResourceNotFoundException;
import com.sat.tracker.model.*;
import com.sat.tracker.repository.CertificateRepository;
import com.sat.tracker.repository.HodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final HodRepository hodRepository;
    private final FileStorageService fileStorageService;
    private final NotificationService notificationService;

    @Transactional
    public CertificateDto upload(Student student, CertificateUploadRequest req, MultipartFile file) {
        if (req.getTitle() == null || req.getTitle().isBlank()
                || req.getEventType() == null || req.getEventType().isBlank()
                || req.getAchievementType() == null || req.getAchievementType().isBlank()) {
            throw new BadRequestException("Title, event type and achievement type are required");
        }

        String storedName = fileStorageService.store(file);
        String fileType = fileStorageService.extractExtension(file.getOriginalFilename());

        Certificate certificate = Certificate.builder()
                .student(student)
                .title(req.getTitle())
                .description(req.getDescription())
                .eventType(req.getEventType())
                .achievementType(req.getAchievementType())
                .filePath(storedName)
                .fileType(fileType)
                .uploadDate(LocalDateTime.now())
                .staffStatus(ReviewStatus.PENDING)
                .hodStatus(ReviewStatus.PENDING)
                .build();

        certificate = certificateRepository.save(certificate);

        if (student.getClassTeacher() != null) {
            notificationService.notifyStaff(student.getClassTeacher(), student, certificate,
                    "New certificate uploaded by " + student.getName() + " for your review.");
        }

        return CertificateDto.from(certificate);
    }

    public List<CertificateDto> forStudent(Long studentId) {
        return certificateRepository.findByStudentId(studentId).stream()
                .map(CertificateDto::from)
                .collect(Collectors.toList());
    }

    public List<CertificateDto> forStaffStudents(List<Long> studentIds) {
        if (studentIds.isEmpty()) return List.of();
        return certificateRepository.findByStudentIdIn(studentIds).stream()
                .map(CertificateDto::from)
                .collect(Collectors.toList());
    }

    public List<CertificateDto> approvedByBranch(String branch) {
        return approvedByBranch(branch, null, null);
    }

    public List<CertificateDto> approvedByBranch(String branch, Integer year, String section) {
        List<Certificate> certs = certificateRepository.findByBranchAndStaffStatus(branch, ReviewStatus.APPROVED);
        if (year != null) {
            certs = certs.stream().filter(c -> java.util.Objects.equals(c.getStudent().getYear(), year)).collect(Collectors.toList());
        }
        if (section != null && !section.isBlank()) {
            certs = certs.stream().filter(c -> section.equalsIgnoreCase(c.getStudent().getSection())).collect(Collectors.toList());
        }
        return certs.stream().map(CertificateDto::from).collect(Collectors.toList());
    }

    @Transactional
    public CertificateDto staffReview(Staff staff, Long certificateId, ReviewRequest req) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));
        Student student = certificate.getStudent();

        if (student.getClassTeacher() == null || !student.getClassTeacher().getId().equals(staff.getId())) {
            throw new AccessDeniedCustomException("You are not the class teacher for this student");
        }

        ReviewStatus status = parseStatus(req.getStatus());
        certificate.setStaffStatus(status);
        certificate.setStaffFeedback(req.getFeedback());
        certificate.setStaffReviewDate(LocalDateTime.now());
        certificate = certificateRepository.save(certificate);
        final Certificate savedCertificate = certificate;

        if (status == ReviewStatus.APPROVED) {
            hodRepository.findByBranch(student.getBranch()).ifPresent(hod ->
                    notificationService.notifyHod(hod, student, savedCertificate,
                            "Certificate approved by class teacher for " + student.getName() + " - waiting for your review."));
        }

        notificationService.notifyStudent(student, certificate,
                "Your certificate '" + certificate.getTitle() + "' has been " + status.name().toLowerCase() + " by your class teacher.");

        return CertificateDto.from(certificate);
    }

    @Transactional
    public CertificateDto hodReview(Hod hod, Long certificateId, ReviewRequest req) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));
        Student student = certificate.getStudent();

        if (!student.getBranch().equalsIgnoreCase(hod.getBranch())) {
            throw new AccessDeniedCustomException("This student is not in your branch");
        }

        ReviewStatus status = parseStatus(req.getStatus());
        certificate.setHodStatus(status);
        certificate.setHodFeedback(req.getFeedback());
        certificate.setHodReviewDate(LocalDateTime.now());
        certificate = certificateRepository.save(certificate);

        notificationService.notifyStudent(student, certificate,
                "Your certificate '" + certificate.getTitle() + "' has been " + status.name().toLowerCase() + " by the HOD.");

        return CertificateDto.from(certificate);
    }

    private ReviewStatus parseStatus(String status) {
        if (status == null) throw new BadRequestException("Status is required");
        try {
            ReviewStatus rs = ReviewStatus.valueOf(status.trim().toUpperCase());
            if (rs == ReviewStatus.PENDING) throw new BadRequestException("Invalid status");
            return rs;
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Status must be 'approved' or 'rejected'");
        }
    }

    public Certificate getForFileAccess(Long certificateId, User currentUser, Student studentOrNull, Staff staffOrNull) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));

        switch (currentUser.getRole()) {
            case STUDENT -> {
                if (studentOrNull == null || !certificate.getStudent().getId().equals(studentOrNull.getId())) {
                    throw new AccessDeniedCustomException("You cannot access this certificate");
                }
            }
            case STAFF -> {
                Student s = certificate.getStudent();
                if (staffOrNull == null || s.getClassTeacher() == null || !s.getClassTeacher().getId().equals(staffOrNull.getId())) {
                    throw new AccessDeniedCustomException("You cannot access this certificate");
                }
            }
            case HOD -> {
                // HODs can view all certificates within the app; branch scoping is handled at listing level.
            }
        }
        return certificate;
    }

    public byte[] readFile(Certificate certificate) {
        byte[] content = fileStorageService.read(certificate.getFilePath());
        if (content == null) {
            throw new ResourceNotFoundException("Certificate file not found on disk");
        }
        return content;
    }
}
