package com.sat.tracker.service;

import com.sat.tracker.dto.NotificationDto;
import com.sat.tracker.model.*;
import com.sat.tracker.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public Notification notifyStudent(Student student, Certificate certificate, String message) {
        Notification n = save(student, null, null, certificate, message);
        push(student.getUser().getUsername(), n);
        return n;
    }

    public Notification notifyStaff(Staff staff, Student student, Certificate certificate, String message) {
        Notification n = save(student, staff, null, certificate, message);
        push(staff.getUser().getUsername(), n);
        return n;
    }

    public Notification notifyHod(Hod hod, Student student, Certificate certificate, String message) {
        Notification n = save(student, null, hod, certificate, message);
        push(hod.getUser().getUsername(), n);
        return n;
    }

    private Notification save(Student student, Staff staff, Hod hod, Certificate certificate, String message) {
        Notification notification = Notification.builder()
                .student(student)
                .staff(staff)
                .hod(hod)
                .certificate(certificate)
                .message(message)
                .read(false)
                .build();
        return notificationRepository.save(notification);
    }

    private void push(String username, Notification notification) {
        messagingTemplate.convertAndSendToUser(username, "/queue/notifications", NotificationDto.from(notification));
    }

    public List<Notification> unreadForStudent(Long studentId) {
        return notificationRepository.findByStudentIdAndReadFalseOrderByCreatedAtDesc(studentId);
    }

    public List<Notification> unreadForStaff(Long staffId) {
        return notificationRepository.findByStaffIdAndReadFalseOrderByCreatedAtDesc(staffId);
    }

    public List<Notification> unreadForHod(Long hodId) {
        return notificationRepository.findByHodIdAndReadFalseOrderByCreatedAtDesc(hodId);
    }

    public List<Notification> allForStudent(Long studentId) {
        return notificationRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
    }

    public List<Notification> allForStaff(Long staffId) {
        return notificationRepository.findByStaffIdOrderByCreatedAtDesc(staffId);
    }

    public List<Notification> allForHod(Long hodId) {
        return notificationRepository.findByHodIdOrderByCreatedAtDesc(hodId);
    }
}
