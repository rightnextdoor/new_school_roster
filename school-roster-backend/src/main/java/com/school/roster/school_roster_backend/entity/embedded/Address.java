package com.school.roster.school_roster_backend.entity.embedded;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Address {
    private String streetAddress;
    private String subdivision;
    private String cityMunicipality;
    private String provinceState;
    private String country;
    private String zipCode;
}