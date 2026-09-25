package com.sat.tracker.controller;

import com.sat.tracker.dto.*;
import com.sat.tracker.exception.ResourceNotFoundException;
import com.sat.tracker.model.Student;
import com.sat.tracker.repository.StudentRepository;
import com.sat.tracker.security.UserPrincipal;
import com.sat.tracker.service.AnalyticsService;
import com.sat.tracker.service.CertificateService;
import com.sat.tracker.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentRepository studentRepository;
    private final CertificateService certificateService;
    private final NotificationService notificationService;
    private final AnalyticsService analyticsService;

    private Student currentStudent(UserPrincipal principal) {
        return studentRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<StudentDashboardDto> dashboard(@AuthenticationPrincipal UserPrincipal principal) {
        Student student = currentStudent(principal);
        List<CertificateDto> certificates = certificateService.forStudent(student.getId());
        List<NotificationDto> notifications = notificationService.unreadForStudent(student.getId())
                .stream().map(NotificationDto::from).collect(Collectors.toList());

        Map<String, Long> stats = new LinkedHashMap<>();
        stats.put("total", (long) certificates.size());
        stats.put("approved", certificates.stream().filter(c -> "approved".equals(c.getHodStatus())).count());
        stats.put("pending", certificates.stream().filter(c -> "pending".equals(c.getHodStatus())).count());
        stats.put("rejected", certificates.stream().filter(c -> "rejected".equals(c.getHodStatus())).count());
        stats.put("winning", certificates.stream().filter(c -> "winning".equals(c.getAchievementType())).count());
        stats.put("participation", certificates.stream().filter(c -> "participation".equals(c.getAchievementType())).count());

        return ResponseEntity.ok(StudentDashboardDto.builder()
                .student(StudentProfileDto.from(student))
                .certificates(certificates)
                .notifications(notifications)
                .stats(stats)
                .build());
    }

    @GetMapping("/profile")
    public ResponseEntity<StudentProfileDto> profile(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(StudentProfileDto.from(currentStudent(principal)));
    }

    @GetMapping("/certificates")
    public ResponseEntity<List<CertificateDto>> certificates(@AuthenticationPrincipal UserPrincipal principal) {
        Student student = currentStudent(principal);
        return ResponseEntity.ok(certificateService.forStudent(student.getId()));
    }

    @PostMapping(value = "/certificates", consumes = "multipart/form-data")
    public ResponseEntity<CertificateDto> upload(@AuthenticationPrincipal UserPrincipal principal,
                                                  @RequestParam String title,
                                                  @RequestParam(required = false) String description,
                                                  @RequestParam String eventType,
                                                  @RequestParam String achievementType,
                                                  @RequestParam("file") MultipartFile file) {
        Student student = currentStudent(principal);
        CertificateUploadRequest req = new CertificateUploadRequest();
        req.setTitle(title);
        req.setDescription(description);
        req.setEventType(eventType);
        req.setAchievementType(achievementType);
        return ResponseEntity.ok(certificateService.upload(student, req, file));
    }

    @GetMapping("/analytics")
    public ResponseEntity<StudentAnalyticsDto> analytics(@AuthenticationPrincipal UserPrincipal principal) {
        Student student = currentStudent(principal);
        return ResponseEntity.ok(analyticsService.getStudentAnalytics(student.getId()));
    }
}
