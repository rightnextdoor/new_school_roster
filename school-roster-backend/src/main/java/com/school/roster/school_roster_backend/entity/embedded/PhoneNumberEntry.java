package com.school.roster.school_roster_backend.entity.embedded;
import com.school.roster.school_roster_backend.entity.enums.PhoneType;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class PhoneNumberEntry {
    private PhoneType type;
    private String number;
}
