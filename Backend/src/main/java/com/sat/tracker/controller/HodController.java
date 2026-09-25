package com.sat.tracker.controller;

import com.sat.tracker.dto.*;
import com.sat.tracker.exception.ResourceNotFoundException;
import com.sat.tracker.model.Hod;
import com.sat.tracker.repository.HodRepository;
import com.sat.tracker.repository.StudentRepository;
import com.sat.tracker.security.UserPrincipal;
import com.sat.tracker.service.AnalyticsService;
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
@RequestMapping("/api/hod")
@RequiredArgsConstructor
public class HodController {

    private final HodRepository hodRepository;
    private final StudentRepository studentRepository;
    private final CertificateService certificateService;
    private final NotificationService notificationService;
    private final AnalyticsService analyticsService;

    private Hod currentHod(UserPrincipal principal) {
        return hodRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("HOD profile not found"));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<HodDashboardDto> dashboard(@AuthenticationPrincipal UserPrincipal principal) {
        Hod hod = currentHod(principal);

        long pendingCount = certificateService.approvedByBranch(hod.getBranch()).stream()
                .filter(c -> "pending".equals(c.getHodStatus())).count();

        List<NotificationDto> notifications = notificationService.unreadForHod(hod.getId())
                .stream().map(NotificationDto::from).collect(Collectors.toList());

        BranchAnalyticsDto analytics = analyticsService.getBranchAnalytics(hod.getBranch(), null, null);

        return ResponseEntity.ok(HodDashboardDto.builder()
                .hod(HodProfileDto.from(hod))
                .pendingCount(pendingCount)
                .notifications(notifications)
                .analytics(analytics)
                .build());
    }

    @GetMapping("/profile")
    public ResponseEntity<HodProfileDto> profile(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(HodProfileDto.from(currentHod(principal)));
    }

    @GetMapping("/approvals")
    public ResponseEntity<Map<String, Object>> approvals(@AuthenticationPrincipal UserPrincipal principal,
                                                           @RequestParam(required = false) Integer year,
                                                           @RequestParam(required = false) String section) {
        Hod hod = currentHod(principal);
        List<CertificateDto> all = certificateService.approvedByBranch(hod.getBranch(), year, section);

        Map<String, Object> response = Map.of(
                "pending", all.stream().filter(c -> "pending".equals(c.getHodStatus())).collect(Collectors.toList()),
                "approved", all.stream().filter(c -> "approved".equals(c.getHodStatus())).collect(Collectors.toList()),
                "rejected", all.stream().filter(c -> "rejected".equals(c.getHodStatus())).collect(Collectors.toList()),
                "students", studentRepository.findByBranch(hod.getBranch()).stream()
                        .map(StudentProfileDto::from).collect(Collectors.toList())
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/certificates/{id}/review")
    public ResponseEntity<CertificateDto> review(@AuthenticationPrincipal UserPrincipal principal,
                                                  @PathVariable("id") Long certificateId,
                                                  @Valid @RequestBody ReviewRequest request) {
        Hod hod = currentHod(principal);
        return ResponseEntity.ok(certificateService.hodReview(hod, certificateId, request));
    }

    @GetMapping("/analytics")
    public ResponseEntity<BranchAnalyticsDto> analytics(@AuthenticationPrincipal UserPrincipal principal,
                                                          @RequestParam(required = false) Integer year,
                                                          @RequestParam(required = false) String section) {
        Hod hod = currentHod(principal);
        return ResponseEntity.ok(analyticsService.getBranchAnalytics(hod.getBranch(), year, section));
    }
}
