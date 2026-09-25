package com.sat.tracker.controller;

import com.sat.tracker.dto.NotificationDto;
import com.sat.tracker.exception.AccessDeniedCustomException;
import com.sat.tracker.exception.ResourceNotFoundException;
import com.sat.tracker.model.*;
import com.sat.tracker.repository.*;
import com.sat.tracker.security.UserPrincipal;
import com.sat.tracker.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;
    private final HodRepository hodRepository;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> list(@AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getId()).orElseThrow();
        List<Notification> notifications = switch (user.getRole()) {
            case STUDENT -> {
                Student s = studentRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
                yield notificationService.allForStudent(s.getId());
            }
            case STAFF -> {
                Staff s = staffRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Staff profile not found"));
                yield notificationService.allForStaff(s.getId());
            }
            case HOD -> {
                Hod h = hodRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("HOD profile not found"));
                yield notificationService.allForHod(h.getId());
            }
        };
        return ResponseEntity.ok(notifications.stream().map(NotificationDto::from).collect(Collectors.toList()));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Map<String, String>> markRead(@AuthenticationPrincipal UserPrincipal principal,
                                                          @PathVariable("id") Long notificationId) {
        User user = userRepository.findById(principal.getId()).orElseThrow();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        boolean owns = switch (user.getRole()) {
            case STUDENT -> notification.getStudent() != null
                    && notification.getStudent().getUser().getId().equals(user.getId());
            case STAFF -> notification.getStaff() != null
                    && notification.getStaff().getUser().getId().equals(user.getId());
            case HOD -> notification.getHod() != null
                    && notification.getHod().getUser().getId().equals(user.getId());
        };

        if (!owns) {
            throw new AccessDeniedCustomException("You cannot modify this notification");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
        return ResponseEntity.ok(Map.of("message", "Notification marked as read"));
    }
}
