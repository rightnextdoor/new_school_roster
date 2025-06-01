package com.school.roster.school_roster_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.school.roster.school_roster_backend.entity.embedded.ScoreDetails;
import com.school.roster.school_roster_backend.entity.enums.StudentGradeStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @JsonBackReference(value = "student-grades")
    private User student;

    @ManyToOne
    @JoinColumn(name = "roster_id")
    @JsonBackReference(value = "roster-grades")
    private Roster roster;

    @Embedded
    private ScoreDetails scoreDetails = new ScoreDetails();

    private Double initialGrade = 0.0;

    @Enumerated(EnumType.STRING)
    private StudentGradeStatus finalStatus;
}
