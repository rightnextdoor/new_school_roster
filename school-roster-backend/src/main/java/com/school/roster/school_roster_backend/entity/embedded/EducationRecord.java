package com.school.roster.school_roster_backend.entity.embedded;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class EducationRecord {
    private String schoolName;
    private Address schoolAddress;
    private String educationLevel; // Example: "Elementary", "High School", "Bachelor's Degree", "Master's Degree"
    private Integer yearGraduated;
}
