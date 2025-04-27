package com.school.roster.school_roster_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.school.roster.school_roster_backend.entity.embedded.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "student_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private LocalDate birthDate;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "streetAddress", column = @Column(name = "birth_streetAddress")),
            @AttributeOverride(name = "subdivision", column = @Column(name = "birth_subdivision")),
            @AttributeOverride(name = "cityMunicipality", column = @Column(name = "birth_cityMunicipality")),
            @AttributeOverride(name = "provinceState", column = @Column(name = "birth_provinceState")),
            @AttributeOverride(name = "country", column = @Column(name = "birth_country")),
            @AttributeOverride(name = "zipCode", column = @Column(name = "birth_zipCode")),
    })
    private Address birthPlace;

    private String motherFirstName;
    private String motherMiddleName;
    private String motherMaidenName;
    private String fatherFirstName;
    private String fatherMiddleName;
    private String fatherLastName;
    private String religion;

    @ElementCollection
    private List<String> dialects;

    @ElementCollection
    private List<PhoneNumberEntry> phoneNumbers;

    @Embedded
    private Address address;

    private String gradeLevel;
    private LocalDate firstAttendanceDate;

    private String schoolPicture;

    @ElementCollection
    private List<SchoolYearHistory> schoolHistories;

    @Embedded
    private NutritionalStatus nutritionalStatus;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User linkedUser;
}
