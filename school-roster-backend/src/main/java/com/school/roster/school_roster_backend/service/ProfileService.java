package com.school.roster.school_roster_backend.service;

import com.school.roster.school_roster_backend.entity.NonStudentProfile;
import com.school.roster.school_roster_backend.entity.StudentProfile;
import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.entity.embedded.AppointmentRecord;
import com.school.roster.school_roster_backend.entity.enums.BMICategory;
import com.school.roster.school_roster_backend.entity.enums.Position;
import com.school.roster.school_roster_backend.entity.enums.SalaryGrade;
import com.school.roster.school_roster_backend.entity.enums.StudentGradeStatus;
import com.school.roster.school_roster_backend.repository.NonStudentProfileRepository;
import com.school.roster.school_roster_backend.repository.StudentProfileRepository;
import com.school.roster.school_roster_backend.repository.UserRepository;
import com.school.roster.school_roster_backend.security.CryptoUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        if (studentProfile.getSchoolHistories() != null) {
            studentProfile.getSchoolHistories().forEach(history -> {
                if (history.getGpa() != null) {
                    history.setGradeStatus(calculateGradeStatus(history.getGpa()));
                }
            });
        }

        if (studentProfile.getNutritionalStatus() != null) {
            updateNutritionalStatus(studentProfile);
        }

        studentProfileRepository.save(studentProfile);
        userRepository.save(user);

        return studentProfile;
    }

    // === Update Student Profile ===
    public StudentProfile updateStudentProfile(Long profileId, StudentProfile updatedData) {
        StudentProfile existingProfile = studentProfileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("StudentProfile not found with ID: " + profileId));

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

        if (existingProfile.getSchoolHistories() != null) {
            existingProfile.getSchoolHistories().forEach(history -> {
                if (history.getGpa() != null) {
                    history.setGradeStatus(calculateGradeStatus(history.getGpa()));
                }
            });
        }

        if (existingProfile.getNutritionalStatus() != null) {
            updateNutritionalStatus(existingProfile);
        }

        return studentProfileRepository.save(existingProfile);
    }

    private StudentGradeStatus calculateGradeStatus(Float gpa) {
        if (gpa == null) return StudentGradeStatus.FAILED;
        if (gpa >= 98) return StudentGradeStatus.WITH_HIGHEST_HONORS;
        if (gpa >= 95) return StudentGradeStatus.WITH_HIGH_HONORS;
        if (gpa >= 90) return StudentGradeStatus.WITH_HONORS;
        if (gpa >= 75) return StudentGradeStatus.PASSED;
        return StudentGradeStatus.FAILED;
    }

    private void updateNutritionalStatus(StudentProfile profile) {
        if (profile.getNutritionalStatus().getHeightInMeters() != null
                && profile.getNutritionalStatus().getWeightInKilograms() != null) {

            Float height = profile.getNutritionalStatus().getHeightInMeters();
            Float weight = profile.getNutritionalStatus().getWeightInKilograms();
            Float bmi = weight / (height * height);
            profile.getNutritionalStatus().setBmi(bmi);

            if (bmi < 16) {
                profile.getNutritionalStatus().setBmiCategory(BMICategory.SEVERELY_WASTED);
            } else if (bmi < 18.5) {
                profile.getNutritionalStatus().setBmiCategory(BMICategory.WASTED);
            } else if (bmi < 25) {
                profile.getNutritionalStatus().setBmiCategory(BMICategory.NORMAL);
            } else if (bmi < 30) {
                profile.getNutritionalStatus().setBmiCategory(BMICategory.OVERWEIGHT);
            } else {
                profile.getNutritionalStatus().setBmiCategory(BMICategory.OBESE);
            }
        }
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

        if (nonStudentProfile.getDependentChildren() != null) {
            nonStudentProfile.getDependentChildren().forEach(this::updateDependentChildAge);
        }

        if (nonStudentProfile.getTaxNumberEncrypted() != null) {
            nonStudentProfile.setTaxNumberEncrypted(CryptoUtils.encrypt(nonStudentProfile.getTaxNumberEncrypted()));
        }
        if (nonStudentProfile.getGsisNumberEncrypted() != null) {
            nonStudentProfile.setGsisNumberEncrypted(CryptoUtils.encrypt(nonStudentProfile.getGsisNumberEncrypted()));
        }
        if (nonStudentProfile.getPhilHealthNumberEncrypted() != null) {
            nonStudentProfile.setPhilHealthNumberEncrypted(CryptoUtils.encrypt(nonStudentProfile.getPhilHealthNumberEncrypted()));
        }
        if (nonStudentProfile.getPagIbigNumberEncrypted() != null) {
            nonStudentProfile.setPagIbigNumberEncrypted(CryptoUtils.encrypt(nonStudentProfile.getPagIbigNumberEncrypted()));
        }

        assignSalaryGrades(nonStudentProfile.getEmploymentAppointments());

        nonStudentProfileRepository.save(nonStudentProfile);
        userRepository.save(user);

        return nonStudentProfile;
    }

    // === Update Non-Student Profile ===
    public NonStudentProfile updateNonStudentProfile(Long profileId, NonStudentProfile updatedData) {
        NonStudentProfile existingProfile = nonStudentProfileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("NonStudentProfile not found with ID: " + profileId));

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
        if (updatedData.getTaxNumberEncrypted() != null) {
            existingProfile.setTaxNumberEncrypted(CryptoUtils.encrypt(updatedData.getTaxNumberEncrypted()));
        }
        if (updatedData.getGsisNumberEncrypted() != null) {
            existingProfile.setGsisNumberEncrypted(CryptoUtils.encrypt(updatedData.getGsisNumberEncrypted()));
        }
        if (updatedData.getPhilHealthNumberEncrypted() != null) {
            existingProfile.setPhilHealthNumberEncrypted(CryptoUtils.encrypt(updatedData.getPhilHealthNumberEncrypted()));
        }
        if (updatedData.getPagIbigNumberEncrypted() != null) {
            existingProfile.setPagIbigNumberEncrypted(CryptoUtils.encrypt(updatedData.getPagIbigNumberEncrypted()));
        }
        existingProfile.setEmploymentAppointments(updatedData.getEmploymentAppointments());
        existingProfile.setEducationalBackground(updatedData.getEducationalBackground());
        existingProfile.setDepartmentOfEducationEmail(updatedData.getDepartmentOfEducationEmail());
        existingProfile.setProfilePicture(updatedData.getProfilePicture());
        existingProfile.setGradeLevel(updatedData.getGradeLevel());

        if (existingProfile.getDependentChildren() != null) {
            existingProfile.getDependentChildren().forEach(this::updateDependentChildAge);
        }

        assignSalaryGrades(existingProfile.getEmploymentAppointments());

        return nonStudentProfileRepository.save(existingProfile);
    }

    private void updateDependentChildAge(com.school.roster.school_roster_backend.entity.embedded.DependentChild child) {
        if (child.getBirthDate() != null) {
            int years = java.time.Period.between(child.getBirthDate(), java.time.LocalDate.now()).getYears();
            child.setAge(years);
        }
    }

    private void assignSalaryGrades(List<AppointmentRecord> appointments) {
        if (appointments != null) {
            for (AppointmentRecord record : appointments) {
                if (record.getPosition() != null) {
                    SalaryGrade salaryGradeInfo = mapPositionToSalaryGrade(record.getPosition());
                    if (salaryGradeInfo != null) {
                        record.setSalaryGrade(salaryGradeInfo.getSalaryGrade());
                        record.setSalaryAmount(salaryGradeInfo.getSalaryAmount());
                    }
                }
            }
        }
    }

    private SalaryGrade mapPositionToSalaryGrade(Position position) {
        try {
            return SalaryGrade.valueOf(position.name());
        } catch (IllegalArgumentException e) {
            return null;
        }
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

    // === Get Profile by Profile ID (Student or NonStudent) ===
    public StudentProfile getStudentProfileById(Long id) {
        return studentProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("StudentProfile not found with ID: " + id));
    }

    public NonStudentProfile getNonStudentProfileById(Long id) {
        return nonStudentProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NonStudentProfile not found with ID: " + id));
    }


    // === Get Profile by User ID ===
    public Object getProfileByUserId(String userId) {
        StudentProfile studentProfile = studentProfileRepository.findAll().stream()
                .filter(p -> p.getLinkedUser() != null && p.getLinkedUser().getId().equals(userId))
                .findFirst()
                .orElse(null);

        if (studentProfile != null) {
            return studentProfile;
        }

        NonStudentProfile nonStudentProfile = nonStudentProfileRepository.findAll().stream()
                .filter(p -> p.getLinkedUser() != null && p.getLinkedUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Profile not found for User ID: " + userId));

        return nonStudentProfile;
    }

    // === Get All Profiles Combined ===
    public List<Object> getAllProfiles() {
        List<Object> profiles = new ArrayList<>();
        profiles.addAll(studentProfileRepository.findAll());
        profiles.addAll(nonStudentProfileRepository.findAll());
        return profiles;
    }
}
