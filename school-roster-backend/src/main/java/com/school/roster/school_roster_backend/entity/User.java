package com.school.roster.school_roster_backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.school.roster.school_roster_backend.entity.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
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
    private String id;  // 12-digit ID

    @Email
    @NotBlank
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(name = "password", nullable = false)
    private String password; // Encrypted later

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "linkedUser", cascade = CascadeType.ALL)
    @JsonManagedReference
    private StudentProfile studentProfile;

    @OneToOne(mappedBy = "linkedUser", cascade = CascadeType.ALL)
    @JsonManagedReference
    private NonStudentProfile nonStudentProfile;
}
