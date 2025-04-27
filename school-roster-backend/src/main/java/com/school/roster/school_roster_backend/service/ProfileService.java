package com.school.roster.school_roster_backend.service;

import com.school.roster.school_roster_backend.entity.NonStudentProfile;
import com.school.roster.school_roster_backend.entity.StudentProfile;
import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.repository.NonStudentProfileRepository;
import com.school.roster.school_roster_backend.repository.StudentProfileRepository;
import com.school.roster.school_roster_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final NonStudentProfileRepository nonStudentProfileRepository;

    // === Create Student Profile ===
    public StudentProfile createStudentProfile(String userId, StudentProfile studentProfile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (user.getStudentProfile() != null) {
            throw new RuntimeException("StudentProfile already exists for this user.");
        }

        studentProfile.setLinkedUser(user);
        user.setStudentProfile(studentProfile);

        studentProfileRepository.save(studentProfile);
        userRepository.save(user);

        return studentProfile;
    }

    // === Update Student Profile ===
    public StudentProfile updateStudentProfile(Long profileId, StudentProfile updatedData) {
        StudentProfile existingProfile = studentProfileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("StudentProfile not found with ID: " + profileId));

        // Update only editable fields
        existingProfile.setFirstName(updatedData.getFirstName());
        existingProfile.setMiddleName(updatedData.getMiddleName());
        existingProfile.setLastName(updatedData.getLastName());
        existingProfile.setGender(updatedData.getGender());
        existingProfile.setBirthDate(updatedData.getBirthDate());
        existingProfile.setBirthPlace(updatedData.getBirthPlace());
        existingProfile.setReligion(updatedData.getReligion());
        existingProfile.setDialects(updatedData.getDialects());
        existingProfile.setPhoneNumbers(updatedData.getPhoneNumbers());
        existingProfile.setGradeLevel(updatedData.getGradeLevel());
        existingProfile.setFirstAttendanceDate(updatedData.getFirstAttendanceDate());
        existingProfile.setSchoolPicture(updatedData.getSchoolPicture());
        existingProfile.setAddress(updatedData.getAddress());
        existingProfile.setSchoolHistories(updatedData.getSchoolHistories());
        existingProfile.setNutritionalStatus(updatedData.getNutritionalStatus());

        return studentProfileRepository.save(existingProfile);
    }

    // === Delete Student Profile ===
    public void deleteStudentProfile(Long profileId) {
        StudentProfile profile = studentProfileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("StudentProfile not found with ID: " + profileId));

        User user = profile.getLinkedUser();
        if (user != null) {
            user.setStudentProfile(null);
            userRepository.save(user);
        }

        studentProfileRepository.delete(profile);
    }

    // === Create Non-Student Profile ===
    public NonStudentProfile createNonStudentProfile(String userId, NonStudentProfile nonStudentProfile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (user.getNonStudentProfile() != null) {
            throw new RuntimeException("NonStudentProfile already exists for this user.");
        }

        nonStudentProfile.setLinkedUser(user);
        user.setNonStudentProfile(nonStudentProfile);

        nonStudentProfileRepository.save(nonStudentProfile);
        userRepository.save(user);

        return nonStudentProfile;
    }

    // === Update Non-Student Profile ===
    public NonStudentProfile updateNonStudentProfile(Long profileId, NonStudentProfile updatedData) {
        NonStudentProfile existingProfile = nonStudentProfileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("NonStudentProfile not found with ID: " + profileId));

        // Update only editable fields
        existingProfile.setFirstName(updatedData.getFirstName());
        existingProfile.setMiddleName(updatedData.getMiddleName());
        existingProfile.setLastName(updatedData.getLastName());
        existingProfile.setGender(updatedData.getGender());
        existingProfile.setBirthDate(updatedData.getBirthDate());
        existingProfile.setBirthPlace(updatedData.getBirthPlace());
        existingProfile.setCivilStatus(updatedData.getCivilStatus());
        existingProfile.setPhoneNumbers(updatedData.getPhoneNumbers());
        existingProfile.setSpouseFirstName(updatedData.getSpouseFirstName());
        existingProfile.setSpouseMiddleName(updatedData.getSpouseMiddleName());
        existingProfile.setSpouseLastName(updatedData.getSpouseLastName());
        existingProfile.setSpouseOccupation(updatedData.getSpouseOccupation());
        existingProfile.setDependentChildren(updatedData.getDependentChildren());
        existingProfile.setTaxNumberEncrypted(updatedData.getTaxNumberEncrypted());
        existingProfile.setGsisNumberEncrypted(updatedData.getGsisNumberEncrypted());
        existingProfile.setPhilHealthNumberEncrypted(updatedData.getPhilHealthNumberEncrypted());
        existingProfile.setPagIbigNumberEncrypted(updatedData.getPagIbigNumberEncrypted());
        existingProfile.setEmploymentAppointments(updatedData.getEmploymentAppointments());
        existingProfile.setEducationalBackground(updatedData.getEducationalBackground());
        existingProfile.setDepartmentOfEducationEmail(updatedData.getDepartmentOfEducationEmail());
        existingProfile.setProfilePicture(updatedData.getProfilePicture());
        existingProfile.setGradeLevel(updatedData.getGradeLevel());

        return nonStudentProfileRepository.save(existingProfile);
    }

    // === Delete Non-Student Profile ===
    public void deleteNonStudentProfile(Long profileId) {
        NonStudentProfile profile = nonStudentProfileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("NonStudentProfile not found with ID: " + profileId));

        User user = profile.getLinkedUser();
        if (user != null) {
            user.setNonStudentProfile(null);
            userRepository.save(user);
        }

        nonStudentProfileRepository.delete(profile);
    }
}
