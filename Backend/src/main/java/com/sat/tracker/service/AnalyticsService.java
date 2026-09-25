package com.sat.tracker.service;

import com.sat.tracker.dto.BranchAnalyticsDto;
import com.sat.tracker.dto.StudentAnalyticsDto;
import com.sat.tracker.dto.StudentStatDto;
import com.sat.tracker.model.Certificate;
import com.sat.tracker.model.ReviewStatus;
import com.sat.tracker.model.Student;
import com.sat.tracker.repository.CertificateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final CertificateRepository certificateRepository;

    public StudentAnalyticsDto getStudentAnalytics(Long studentId) {
        List<Certificate> certificates = certificateRepository.findByStudentId(studentId);

        Map<String, Long> eventTypeData = certificates.stream()
                .collect(Collectors.groupingBy(Certificate::getEventType, Collectors.counting()));

        Map<String, Long> achievementData = new LinkedHashMap<>();
        achievementData.put("winning", certificates.stream().filter(c -> "winning".equalsIgnoreCase(c.getAchievementType())).count());
        achievementData.put("participation", certificates.stream().filter(c -> "participation".equalsIgnoreCase(c.getAchievementType())).count());

        Map<String, Long> statusData = new LinkedHashMap<>();
        statusData.put("approved", certificates.stream().filter(c -> c.getHodStatus() == ReviewStatus.APPROVED).count());
        statusData.put("pending", certificates.stream().filter(c -> c.getHodStatus() == ReviewStatus.PENDING).count());
        statusData.put("rejected", certificates.stream().filter(c -> c.getHodStatus() == ReviewStatus.REJECTED).count());

        return StudentAnalyticsDto.builder()
                .totalCertificates(certificates.size())
                .eventTypeData(eventTypeData)
                .achievementData(achievementData)
                .statusData(statusData)
                .build();
    }

    /**
     * Branch-wide analytics, optionally filtered by year and/or section.
     * Only certificates that have been fully approved (staff + HOD) count towards the stats,
     * mirroring the original hod_status == 'approved' filter.
     */
    public BranchAnalyticsDto getBranchAnalytics(String branch, Integer year, String section) {
        List<Certificate> approved = certificateRepository.findByBranchAndHodStatusApprovedForAnalytics(branch, ReviewStatus.APPROVED);

        if (year != null) {
            approved = approved.stream().filter(c -> Objects.equals(c.getStudent().getYear(), year)).collect(Collectors.toList());
        }
        if (section != null && !section.isBlank()) {
            approved = approved.stream().filter(c -> section.equalsIgnoreCase(c.getStudent().getSection())).collect(Collectors.toList());
        }

        Map<Long, List<Certificate>> byStudent = approved.stream()
                .collect(Collectors.groupingBy(c -> c.getStudent().getId()));

        List<StudentStatDto> studentStats = new ArrayList<>();
        for (Map.Entry<Long, List<Certificate>> entry : byStudent.entrySet()) {
            Student s = entry.getValue().get(0).getStudent();
            long winning = entry.getValue().stream().filter(c -> "winning".equalsIgnoreCase(c.getAchievementType())).count();
            long participation = entry.getValue().stream().filter(c -> "participation".equalsIgnoreCase(c.getAchievementType())).count();
            studentStats.add(StudentStatDto.builder()
                    .studentId(s.getId())
                    .name(s.getName())
                    .section(s.getSection())
                    .year(s.getYear())
                    .certificateCount(entry.getValue().size())
                    .winningCount(winning)
                    .participationCount(participation)
                    .build());
        }

        Map<String, Long> eventTypeData = approved.stream()
                .collect(Collectors.groupingBy(Certificate::getEventType, Collectors.counting()));

        Map<String, Long> sectionPerformance = approved.stream()
                .collect(Collectors.groupingBy(c -> c.getStudent().getSection(), Collectors.counting()));

        List<StudentStatDto> topPerformers = studentStats.stream()
                .sorted(Comparator.comparingLong(StudentStatDto::getWinningCount)
                        .thenComparingLong(StudentStatDto::getCertificateCount)
                        .reversed())
                .limit(5)
                .collect(Collectors.toList());

        return BranchAnalyticsDto.builder()
                .studentStats(studentStats)
                .eventTypeData(eventTypeData)
                .sectionPerformance(sectionPerformance)
                .topPerformers(topPerformers)
                .totalStudents(studentStats.size())
                .totalCertificates(approved.size())
                .totalWinning(studentStats.stream().mapToLong(StudentStatDto::getWinningCount).sum())
                .totalParticipation(studentStats.stream().mapToLong(StudentStatDto::getParticipationCount).sum())
                .build();
    }
}
