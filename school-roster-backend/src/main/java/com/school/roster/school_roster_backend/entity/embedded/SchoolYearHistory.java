package com.school.roster.school_roster_backend.entity.embedded;

import com.school.roster.school_roster_backend.entity.enums.StudentGradeStatus;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Embeddable
public class SchoolYearHistory {
    private String schoolName;
    private Address schoolAddress;
    private String gradeLevel;
    private LocalDate schoolYearStart;
    private LocalDate schoolYearEnd;

    private List<String> sectionNicknames;
    private boolean completed;
    private Float gpa;
    private StudentGradeStatus gradeStatus;
}
