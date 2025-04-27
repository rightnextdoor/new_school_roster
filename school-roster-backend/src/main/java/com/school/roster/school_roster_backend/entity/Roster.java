package com.school.roster.school_roster_backend.entity;

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
    @JsonManagedReference
    private User teacher; // Only TEACHER or TEACHER_LEAD

    @ManyToMany
    @JoinTable(
            name = "roster_students",
            joinColumns = @JoinColumn(name = "roster_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    @JsonManagedReference
    private List<User> students = new ArrayList<>(); // Only STUDENT role

    @OneToMany(mappedBy = "roster", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Grade> grades = new ArrayList<>();

    private Float classGpa; // Average GPA of all students
}
