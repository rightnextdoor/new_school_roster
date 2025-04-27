package com.school.roster.school_roster_backend.entity.embedded;

import com.school.roster.school_roster_backend.entity.enums.BMICategory;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class NutritionalStatus {
    private Float heightInMeters;
    private Float weightInKilograms;
    private Float bmi;
    private BMICategory bmiCategory;
}