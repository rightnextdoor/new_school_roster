package com.school.roster.school_roster_backend.entity.embedded;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.time.LocalDate;

@Data
@Embeddable
public class DependentChild {
    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDate birthDate;
    private Integer age;
}
