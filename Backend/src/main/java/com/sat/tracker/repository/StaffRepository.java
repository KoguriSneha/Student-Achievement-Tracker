package com.sat.tracker.repository;

import com.sat.tracker.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> {
    Optional<Staff> findByUserId(Long userId);
    Optional<Staff> findByStaffId(String staffId);
    boolean existsByStaffId(String staffId);

    Optional<Staff> findFirstByBranchAndSection(String branch, String section);
}

