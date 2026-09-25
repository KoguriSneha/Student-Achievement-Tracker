package com.sat.tracker.config;

import com.sat.tracker.model.*;
import com.sat.tracker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds a handful of demo accounts (one per role, across two branches) so the
 * application can be explored immediately after startup, mirroring seed_db.py
 * from the original Flask project.
 *
 * Demo credentials (password is the same as the username for every account):
 *   hod_cse / hod_cse       -> HOD, CSE branch
 *   staff_cse / staff_cse   -> Staff (class teacher), CSE-A, year 2
 *   student1 / student1     -> Student, CSE-A, year 2 (class teacher: staff_cse)
 *   student2 / student2     -> Student, CSE-A, year 2 (class teacher: staff_cse)
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;
    private final HodRepository hodRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.enabled:true}")
    private boolean seedEnabled;

    @Override
    @Transactional
    public void run(String... args) {
        if (!seedEnabled || userRepository.count() > 0) {
            return;
        }

        User hodUser = userRepository.save(User.builder()
                .username("hod_cse").email("hod.cse@example.edu")
                .passwordHash(passwordEncoder.encode("hod_cse")).role(Role.HOD).build());
        Hod hod = hodRepository.save(Hod.builder()
                .user(hodUser).name("Dr. Ramesh Kumar").hodId("HOD00CSE1").branch("CSE").build());

        User staffUser = userRepository.save(User.builder()
                .username("staff_cse").email("staff.cse@example.edu")
                .passwordHash(passwordEncoder.encode("staff_cse")).role(Role.STAFF).build());
        Staff staff = staffRepository.save(Staff.builder()
                .user(staffUser).name("Prof. Anita Rao").staffId("STF00CSE1")
                .branch("CSE").section("A").year(2).build());

        User student1User = userRepository.save(User.builder()
                .username("student1").email("student1@example.edu")
                .passwordHash(passwordEncoder.encode("student1")).role(Role.STUDENT).build());
        studentRepository.save(Student.builder()
                .user(student1User).name("Aarav Sharma").studentId("23251A05A1")
                .branch("CSE").section("A").year(2).classTeacher(staff).build());

        User student2User = userRepository.save(User.builder()
                .username("student2").email("student2@example.edu")
                .passwordHash(passwordEncoder.encode("student2")).role(Role.STUDENT).build());
        studentRepository.save(Student.builder()
                .user(student2User).name("Priya Patel").studentId("23251A05A2")
                .branch("CSE").section("A").year(2).classTeacher(staff).build());
    }
}
