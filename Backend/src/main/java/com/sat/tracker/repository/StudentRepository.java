package com.sat.tracker.repository;

import com.sat.tracker.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUserId(Long userId);
    Optional<Student> findByStudentId(String studentId);
    List<Student> findByClassTeacherId(Long staffId);
    List<Student> findByBranch(String branch);
    boolean existsByStudentId(String studentId);

    List<Student> findByBranchAndSectionAndClassTeacherIsNull(String branch, String section);
}