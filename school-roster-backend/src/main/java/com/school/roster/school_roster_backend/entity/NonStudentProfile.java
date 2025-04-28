package com.school.roster.school_roster_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.school.roster.school_roster_backend.entity.embedded.*;
import com.school.roster.school_roster_backend.entity.enums.CivilStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "non_student_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NonStudentProfile {

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

    @Embedded
    private Address address;

    @ElementCollection
    private List<PhoneNumberEntry> phoneNumbers;

    @Enumerated(EnumType.STRING)
    private CivilStatus civilStatus;

    private String spouseFirstName;
    private String spouseMiddleName;
    private String spouseLastName;
    private String spouseOccupation;

    @ElementCollection
    private List<DependentChild> dependentChildren;

    @Column(name = "tax_number", length = 255)
    private String taxNumberEncrypted;

    @Column(name = "gsis_number", length = 255)
    private String gsisNumberEncrypted;

    @Column(name = "philhealth_number", length = 255)
    private String philHealthNumberEncrypted;

    @Column(name = "pagibig_number", length = 255)
    private String pagIbigNumberEncrypted;

    @ElementCollection
    private List<AppointmentRecord> employmentAppointments;

    @ElementCollection
    private List<EducationRecord> educationalBackground;

    private String departmentOfEducationEmail;

    private String profilePicture;

    private String gradeLevel; // Required for TEACHER and TEACHER_LEAD

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference(value = "nonStudentProfile-user")
    private User linkedUser;

}
