package com.school.roster.school_roster_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @ElementCollection
    @CollectionTable(name = "grade_performance_scores", joinColumns = @JoinColumn(name = "grade_id"))
    private List<Float> performanceScores = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "grade_quiz_scores", joinColumns = @JoinColumn(name = "grade_id"))
    private List<Float> quizScores = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "grade_quarterly_exam_scores", joinColumns = @JoinColumn(name = "grade_id"))
    private List<Float> quarterlyExamScores = new ArrayList<>();

    private Float finalGpa;
}
