// package com.sat.tracker.service;

// import com.sat.tracker.dto.*;
// import com.sat.tracker.exception.BadRequestException;
// import com.sat.tracker.model.*;
// import com.sat.tracker.repository.*;
// import com.sat.tracker.security.JwtUtil;
// import lombok.RequiredArgsConstructor;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.BadCredentialsException;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.Map;

// @Service
// @RequiredArgsConstructor
// public class AuthService {

//     private final UserRepository userRepository;
//     private final StudentRepository studentRepository;
//     private final StaffRepository staffRepository;
//     private final HodRepository hodRepository;
//     private final PasswordEncoder passwordEncoder;
//     private final AuthenticationManager authenticationManager;
//     private final JwtUtil jwtUtil;

//     public LoginResponse login(LoginRequest request) {
//         try {
//             authenticationManager.authenticate(
//                     new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
//             );
//         } catch (BadCredentialsException ex) {
//             throw new BadRequestException("Invalid username or password");
//         }

//         User user = userRepository.findByUsername(request.getUsername())
//                 .orElseThrow(() -> new BadRequestException("Invalid username or password"));

//         String name = resolveDisplayName(user);

//         String token = jwtUtil.generateToken(user.getUsername(), Map.of(
//                 "role", user.getRole().name(),
//                 "userId", user.getId()
//         ));

//         return new LoginResponse(token, user.getId(), user.getUsername(), user.getRole().name(), name);
//     }

//     private String resolveDisplayName(User user) {
//         return switch (user.getRole()) {
//             case STUDENT -> studentRepository.findByUserId(user.getId()).map(Student::getName).orElse(user.getUsername());
//             case STAFF -> staffRepository.findByUserId(user.getId()).map(Staff::getName).orElse(user.getUsername());
//             case HOD -> hodRepository.findByUserId(user.getId()).map(Hod::getName).orElse(user.getUsername());
//         };
//     }

//     @Transactional
//     public void changePassword(Long userId, ChangePasswordRequest request) {
//         User user = userRepository.findById(userId)
//                 .orElseThrow(() -> new BadRequestException("User not found"));
//         if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
//             throw new BadRequestException("Current password is incorrect");
//         }
//         user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
//         userRepository.save(user);
//     }

//     @Transactional
//     public Student registerStudent(RegisterStudentRequest req) {
//         validateNewUser(req.getUsername(), req.getEmail());
//         if (studentRepository.existsByStudentId(req.getStudentId())) {
//             throw new BadRequestException("Student ID already exists");
//         }
//         User user = userRepository.save(User.builder()
//                 .username(req.getUsername())
//                 .email(req.getEmail())
//                 .passwordHash(passwordEncoder.encode(req.getPassword()))
//                 .role(Role.STUDENT)
//                 .build());

//         Staff classTeacher = null;
//         if (req.getClassTeacherId() != null) {
//             classTeacher = staffRepository.findById(req.getClassTeacherId())
//                     .orElseThrow(() -> new BadRequestException("Class teacher not found"));
//         }

//         Student student = Student.builder()
//                 .user(user)
//                 .name(req.getName())
//                 .studentId(req.getStudentId())
//                 .branch(req.getBranch())
//                 .section(req.getSection())
//                 .year(req.getYear())
//                 .classTeacher(classTeacher)
//                 .build();
//         return studentRepository.save(student);
//     }

//     @Transactional
//     public Staff registerStaff(RegisterStaffRequest req) {
//         validateNewUser(req.getUsername(), req.getEmail());
//         if (staffRepository.existsByStaffId(req.getStaffId())) {
//             throw new BadRequestException("Staff ID already exists");
//         }
//         User user = userRepository.save(User.builder()
//                 .username(req.getUsername())
//                 .email(req.getEmail())
//                 .passwordHash(passwordEncoder.encode(req.getPassword()))
//                 .role(Role.STAFF)
//                 .build());

//         Staff staff = Staff.builder()
//                 .user(user)
//                 .name(req.getName())
//                 .staffId(req.getStaffId())
//                 .branch(req.getBranch())
//                 .section(req.getSection())
//                 .year(req.getYear())
//                 .build();
//         return staffRepository.save(staff);
//     }

//     @Transactional
//     public Hod registerHod(RegisterHodRequest req) {
//         validateNewUser(req.getUsername(), req.getEmail());
//         if (hodRepository.existsByHodId(req.getHodId())) {
//             throw new BadRequestException("HOD ID already exists");
//         }
//         User user = userRepository.save(User.builder()
//                 .username(req.getUsername())
//                 .email(req.getEmail())
//                 .passwordHash(passwordEncoder.encode(req.getPassword()))
//                 .role(Role.HOD)
//                 .build());

//         Hod hod = Hod.builder()
//                 .user(user)
//                 .name(req.getName())
//                 .hodId(req.getHodId())
//                 .branch(req.getBranch())
//                 .build();
//         return hodRepository.save(hod);
//     }

//     private void validateNewUser(String username, String email) {
//         if (userRepository.existsByUsername(username)) {
//             throw new BadRequestException("Username already taken");
//         }
//         if (userRepository.existsByEmail(email)) {
//             throw new BadRequestException("Email already registered");
//         }
//     }
// }


package com.sat.tracker.service;

import com.sat.tracker.dto.*;
import com.sat.tracker.exception.BadRequestException;
import com.sat.tracker.model.*;
import com.sat.tracker.repository.*;
import com.sat.tracker.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;
    private final HodRepository hodRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new BadRequestException("Invalid username or password");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("Invalid username or password"));

        String name = resolveDisplayName(user);

        String token = jwtUtil.generateToken(user.getUsername(), Map.of(
                "role", user.getRole().name(),
                "userId", user.getId()
        ));

        return new LoginResponse(token, user.getId(), user.getUsername(), user.getRole().name(), name);
    }

    private String resolveDisplayName(User user) {
        return switch (user.getRole()) {
            case STUDENT -> studentRepository.findByUserId(user.getId()).map(Student::getName).orElse(user.getUsername());
            case STAFF -> staffRepository.findByUserId(user.getId()).map(Staff::getName).orElse(user.getUsername());
            case HOD -> hodRepository.findByUserId(user.getId()).map(Hod::getName).orElse(user.getUsername());
        };
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public Student registerStudent(RegisterStudentRequest req) {
        validateNewUser(req.getUsername(), req.getEmail());
        if (studentRepository.existsByStudentId(req.getStudentId())) {
            throw new BadRequestException("Student ID already exists");
        }
        User user = userRepository.save(User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(Role.STUDENT)
                .build());

        Staff classTeacher;
        if (req.getClassTeacherId() != null) {
            // Explicit teacher chosen (e.g. from a future "pick your teacher" dropdown) - honor it.
            classTeacher = staffRepository.findById(req.getClassTeacherId())
                    .orElseThrow(() -> new BadRequestException("Class teacher not found"));
        } else {
            // No explicit teacher given: auto-assign dynamically by matching an existing staff
            // member in the same branch + section. If none exists yet, the student is created
            // with no class teacher (correct - there's genuinely nobody to assign).
            classTeacher = staffRepository.findFirstByBranchAndSection(req.getBranch(), req.getSection())
                    .orElse(null);
        }

        Student student = Student.builder()
                .user(user)
                .name(req.getName())
                .studentId(req.getStudentId())
                .branch(req.getBranch())
                .section(req.getSection())
                .year(req.getYear())
                .classTeacher(classTeacher)
                .build();
        return studentRepository.save(student);
    }

    @Transactional
    public Staff registerStaff(RegisterStaffRequest req) {
        validateNewUser(req.getUsername(), req.getEmail());
        if (staffRepository.existsByStaffId(req.getStaffId())) {
            throw new BadRequestException("Staff ID already exists");
        }
        User user = userRepository.save(User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(Role.STAFF)
                .build());

        Staff staff = Staff.builder()
                .user(user)
                .name(req.getName())
                .staffId(req.getStaffId())
                .branch(req.getBranch())
                .section(req.getSection())
                .year(req.getYear())
                .build();
        staff = staffRepository.save(staff);
        final Staff savedStaff = staff;

        // Dynamic backfill: if students in this branch + section already exist without a class
        // teacher (registered before any staff was available), link them to this new staff member now.
        studentRepository.findByBranchAndSectionAndClassTeacherIsNull(req.getBranch(), req.getSection())
                .forEach(student -> {
                    student.setClassTeacher(savedStaff);
                    studentRepository.save(student);
                });

        return savedStaff;
    }

    @Transactional
    public Hod registerHod(RegisterHodRequest req) {
        validateNewUser(req.getUsername(), req.getEmail());
        if (hodRepository.existsByHodId(req.getHodId())) {
            throw new BadRequestException("HOD ID already exists");
        }
        User user = userRepository.save(User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(Role.HOD)
                .build());

        Hod hod = Hod.builder()
                .user(user)
                .name(req.getName())
                .hodId(req.getHodId())
                .branch(req.getBranch())
                .build();
        // No explicit linking needed for HOD - certificates are matched to a HOD by branch at
        // review time (see CertificateService.staffReview), so a HOD automatically "covers"
        // every student and every staff member in their branch as soon as they register.
        return hodRepository.save(hod);
    }

    private void validateNewUser(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("Username already taken");
        }
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already registered");
        }
    }
}