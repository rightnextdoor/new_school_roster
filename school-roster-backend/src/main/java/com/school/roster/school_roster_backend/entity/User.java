package com.school.roster.school_roster_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.school.roster.school_roster_backend.entity.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(name = "id", unique = true, nullable = false, length = 12)
    private String id;

    @Email
    @NotBlank
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(name = "password", nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "linkedUser", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "studentProfile-user")
    private StudentProfile studentProfile;

    @OneToOne(mappedBy = "linkedUser", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "nonStudentProfile-user")
    private NonStudentProfile nonStudentProfile;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "teacher-rosters")
    private List<Roster> teachingRosters;

    @ManyToMany(mappedBy = "students", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Roster> studentRosters;
}
