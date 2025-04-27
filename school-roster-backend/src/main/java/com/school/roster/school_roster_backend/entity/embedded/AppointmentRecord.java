package com.school.roster.school_roster_backend.entity.embedded;

import com.school.roster.school_roster_backend.entity.enums.AppointmentType;
import com.school.roster.school_roster_backend.entity.enums.Position;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.time.LocalDate;

@Data
@Embeddable
public class AppointmentRecord {
    private AppointmentType appointmentType;
    private Position position;
    private LocalDate dateIssued;
}
