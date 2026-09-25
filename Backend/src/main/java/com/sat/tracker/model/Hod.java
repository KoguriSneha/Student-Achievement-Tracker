package com.sat.tracker.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hod")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "hod_id", nullable = false, unique = true, length = 10)
    private String hodId;

    @Column(nullable = false, length = 50)
    private String branch;
}
