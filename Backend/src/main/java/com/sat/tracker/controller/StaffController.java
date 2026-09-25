package com.sat.tracker.controller;

import com.sat.tracker.dto.*;
import com.sat.tracker.exception.ResourceNotFoundException;
import com.sat.tracker.model.Staff;
import com.sat.tracker.model.Student;
import com.sat.tracker.repository.StaffRepository;
import com.sat.tracker.repository.StudentRepository;
import com.sat.tracker.security.UserPrincipal;
import com.sat.tracker.service.CertificateService;
import com.sat.tracker.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffRepository staffRepository;
    private final StudentRepository studentRepository;
    private final CertificateService certificateService;
    private final NotificationService notificationService;

    private Staff currentStaff(UserPrincipal principal) {
        return staffRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Staff profile not found"));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<StaffDashboardDto> dashboard(@AuthenticationPrincipal UserPrincipal principal) {
        Staff staff = currentStaff(principal);
        List<Student> students = studentRepository.findByClassTeacherId(staff.getId());
        List<Long> studentIds = students.stream().map(Student::getId).collect(Collectors.toList());

        long pendingCount = certificateService.forStaffStudents(studentIds).stream()
                .filter(c -> "pending".equals(c.getStaffStatus())).count();

        List<NotificationDto> notifications = notificationService.unreadForStaff(staff.getId())
                .stream().map(NotificationDto::from).collect(Collectors.toList());

        return ResponseEntity.ok(StaffDashboardDto.builder()
                .staff(StaffProfileDto.from(staff))
                .students(students.stream().map(StudentProfileDto::from).collect(Collectors.toList()))
                .pendingCount(pendingCount)
                .notifications(notifications)
                .build());
    }

    @GetMapping("/profile")
    public ResponseEntity<StaffProfileDto> profile(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(StaffProfileDto.from(currentStaff(principal)));
    }

    @GetMapping("/approvals")
    public ResponseEntity<Map<String, List<CertificateDto>>> approvals(@AuthenticationPrincipal UserPrincipal principal) {
        Staff staff = currentStaff(principal);
        List<Long> studentIds = studentRepository.findByClassTeacherId(staff.getId())
                .stream().map(Student::getId).collect(Collectors.toList());
        List<CertificateDto> all = certificateService.forStaffStudents(studentIds);

        Map<String, List<CertificateDto>> grouped = Map.of(
                "pending", all.stream().filter(c -> "pending".equals(c.getStaffStatus())).collect(Collectors.toList()),
                "approved", all.stream().filter(c -> "approved".equals(c.getStaffStatus())).collect(Collectors.toList()),
                "rejected", all.stream().filter(c -> "rejected".equals(c.getStaffStatus())).collect(Collectors.toList())
        );
        return ResponseEntity.ok(grouped);
    }

    @PostMapping("/certificates/{id}/review")
    public ResponseEntity<CertificateDto> review(@AuthenticationPrincipal UserPrincipal principal,
                                                  @PathVariable("id") Long certificateId,
                                                  @Valid @RequestBody ReviewRequest request) {
        Staff staff = currentStaff(principal);
        return ResponseEntity.ok(certificateService.staffReview(staff, certificateId, request));
    }
}
