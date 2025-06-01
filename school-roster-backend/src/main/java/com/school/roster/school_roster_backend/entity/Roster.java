package com.school.roster.school_roster_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rosters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Roster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subjectName;
    private String period;
    private String nickname;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    @JsonBackReference(value = "teacher-rosters")
    private User teacher;

    @ManyToMany
    @JoinTable(
            name = "roster_students",
            joinColumns = @JoinColumn(name = "roster_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    @JsonBackReference(value = "student-rosters")
    private List<User> students = new ArrayList<>();

    @OneToOne(
            mappedBy = "roster",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonManagedReference(value = "roster-hps")
    private HighestPossibleScore highestPossibleScore;

    @OneToMany(mappedBy = "roster", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "roster-grades")
    private List<Grade> grades = new ArrayList<>();

    private Float classGpa;
}
