package com.sat.tracker.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "staff")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "staff_id", nullable = false, unique = true, length = 10)
    private String staffId;

    @Column(nullable = false, length = 50)
    private String branch;

    @Column(nullable = false, length = 10)
    private String section;

    /** Year (1-4) of students this staff is the class teacher for */
    @Column
    private Integer year;
}
