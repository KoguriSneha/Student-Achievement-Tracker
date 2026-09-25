package com.sat.tracker.repository;

import com.sat.tracker.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByStudentIdAndReadFalseOrderByCreatedAtDesc(Long studentId);
    List<Notification> findByStaffIdAndReadFalseOrderByCreatedAtDesc(Long staffId);
    List<Notification> findByHodIdAndReadFalseOrderByCreatedAtDesc(Long hodId);

    List<Notification> findByStudentIdOrderByCreatedAtDesc(Long studentId);
    List<Notification> findByStaffIdOrderByCreatedAtDesc(Long staffId);
    List<Notification> findByHodIdOrderByCreatedAtDesc(Long hodId);
}
