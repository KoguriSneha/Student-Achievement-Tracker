package com.sat.tracker.repository;

import com.sat.tracker.model.Hod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HodRepository extends JpaRepository<Hod, Long> {
    Optional<Hod> findByUserId(Long userId);
    Optional<Hod> findByHodId(String hodId);
    Optional<Hod> findByBranch(String branch);
    boolean existsByHodId(String hodId);
}
