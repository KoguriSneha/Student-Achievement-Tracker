package com.sat.tracker.controller;

import com.sat.tracker.dto.*;
import com.sat.tracker.security.UserPrincipal;
import com.sat.tracker.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@AuthenticationPrincipal UserPrincipal principal,
                                                                @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(principal.getId(), request);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    /**
     * Registration endpoints are intentionally open (no admin role exists in this project),
     * intended for initial account provisioning / demo seeding. In a production rollout these
     * would typically be locked behind an admin-only role or an invite-token flow.
     */
    @PostMapping("/register/student")
    public ResponseEntity<Map<String, Object>> registerStudent(@Valid @RequestBody RegisterStudentRequest request) {
        var student = authService.registerStudent(request);
        return ResponseEntity.ok(Map.of("id", student.getId(), "message", "Student registered successfully"));
    }

    @PostMapping("/register/staff")
    public ResponseEntity<Map<String, Object>> registerStaff(@Valid @RequestBody RegisterStaffRequest request) {
        var staff = authService.registerStaff(request);
        return ResponseEntity.ok(Map.of("id", staff.getId(), "message", "Staff registered successfully"));
    }

    @PostMapping("/register/hod")
    public ResponseEntity<Map<String, Object>> registerHod(@Valid @RequestBody RegisterHodRequest request) {
        var hod = authService.registerHod(request);
        return ResponseEntity.ok(Map.of("id", hod.getId(), "message", "HOD registered successfully"));
    }
}
