package com.sat.tracker.controller;

import com.sat.tracker.model.Certificate;
import com.sat.tracker.model.Staff;
import com.sat.tracker.model.Student;
import com.sat.tracker.model.User;
import com.sat.tracker.repository.StaffRepository;
import com.sat.tracker.repository.StudentRepository;
import com.sat.tracker.repository.UserRepository;
import com.sat.tracker.security.UserPrincipal;
import com.sat.tracker.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateFileController {

    private final CertificateService certificateService;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;

    @GetMapping("/{id}/file")
    public ResponseEntity<byte[]> viewCertificateFile(@AuthenticationPrincipal UserPrincipal principal,
                                                        @PathVariable("id") Long certificateId,
                                                        @RequestParam(required = false, defaultValue = "false") boolean download) {
        User user = userRepository.findById(principal.getId()).orElseThrow();
        Student student = studentRepository.findByUserId(user.getId()).orElse(null);
        Staff staff = staffRepository.findByUserId(user.getId()).orElse(null);

        Certificate certificate = certificateService.getForFileAccess(certificateId, user, student, staff);
        byte[] content = certificateService.readFile(certificate);

        MediaType mediaType = "pdf".equalsIgnoreCase(certificate.getFileType())
                ? MediaType.APPLICATION_PDF
                : MediaType.IMAGE_JPEG;

        HttpHeaders headers = new HttpHeaders();
        String disposition = (download ? "attachment" : "inline") + "; filename=\"" + certificate.getFilePath() + "\"";
        headers.add(HttpHeaders.CONTENT_DISPOSITION, disposition);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(mediaType)
                .body(content);
    }
}
