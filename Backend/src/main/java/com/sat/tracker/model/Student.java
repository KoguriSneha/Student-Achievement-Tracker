package com.sat.tracker.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student")
@Getter
@Setter
@NoArgsConstructor  
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "student_id", nullable = false, unique = true, length = 10)
    private String studentId;

    @Column(nullable = false, length = 50)
    private String branch;

    @Column(nullable = false, length = 10)
    private String section;

    @Column(nullable = false)
    private Integer year;

    @ManyToOne
    @JoinColumn(name = "class_teacher_id")
    private Staff classTeacher;
}
