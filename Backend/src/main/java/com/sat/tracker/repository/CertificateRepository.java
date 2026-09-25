package com.sat.tracker.repository;

import com.sat.tracker.model.Certificate;
import com.sat.tracker.model.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    List<Certificate> findByStudentId(Long studentId);

    List<Certificate> findByStudentIdIn(List<Long> studentIds);

    List<Certificate> findByStudentIdAndStaffStatus(Long studentId, ReviewStatus staffStatus);

    @Query("select c from Certificate c join fetch c.student s where s.branch = :branch and c.staffStatus = :staffStatus")
    List<Certificate> findByBranchAndStaffStatus(String branch, ReviewStatus staffStatus);

    @Query("select c from Certificate c join fetch c.student s where s.branch = :branch and c.hodStatus = :hodStatus")
    List<Certificate> findByBranchAndHodStatusApprovedForAnalytics(String branch, ReviewStatus hodStatus);
}
