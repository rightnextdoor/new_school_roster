package com.school.roster.school_roster_backend.service;

import com.school.roster.school_roster_backend.entity.*;
import com.school.roster.school_roster_backend.entity.embedded.ScoreDetails;
import com.school.roster.school_roster_backend.entity.embedded.*;
import com.school.roster.school_roster_backend.entity.enums.*;
import com.school.roster.school_roster_backend.repository.NonStudentProfileRepository;
import com.school.roster.school_roster_backend.repository.StudentProfileRepository;
import com.school.roster.school_roster_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ProfileServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private StudentProfileRepository studentProfileRepository;
    @Mock private NonStudentProfileRepository nonStudentProfileRepository;

    @InjectMocks
    private ProfileService profileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // === Student Profile Tests ===

    @Test
    void createStudentProfile_shouldCalculateBMI() {
        User user = new User();
        user.setId("123456789012");

        StudentProfile profile = new StudentProfile();
        NutritionalStatus nutritionalStatus = new NutritionalStatus();
        nutritionalStatus.setHeightInCentimeters(1.7f);
        nutritionalStatus.setWeightInKilograms(70f);
        profile.setNutritionalStatus(nutritionalStatus);

        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
        when(studentProfileRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        StudentProfile created = profileService.createStudentProfile(user.getId(), profile);

        assertNotNull(created.getNutritionalStatus().getBmi());
        assertEquals(BMICategory.OBESE, created.getNutritionalStatus().getBmiCategory());
    }

    @Test
    void createStudentProfile_userNotFound_shouldThrow() {
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () ->
                profileService.createStudentProfile("badId", new StudentProfile())
        );
    }

    @Test
    void updateStudentProfile_shouldRecalculateBMI() {
        StudentProfile profile = new StudentProfile();
        profile.setId(1L);
        NutritionalStatus nutritionalStatus = new NutritionalStatus();
        nutritionalStatus.setHeightInCentimeters(1.8f);
        nutritionalStatus.setWeightInKilograms(80f);
        profile.setNutritionalStatus(nutritionalStatus);

        when(studentProfileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(studentProfileRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        StudentProfile updated = profileService.updateStudentProfile(1L, profile);
        assertNotNull(updated.getNutritionalStatus().getBmi());
        assertEquals(BMICategory.OBESE, updated.getNutritionalStatus().getBmiCategory());
    }

    @Test
    void deleteStudentProfile_shouldUnlinkAndDelete() {
        StudentProfile profile = new StudentProfile();
        User user = new User();
        profile.setLinkedUser(user);

        when(studentProfileRepository.findById(1L)).thenReturn(Optional.of(profile));

        profileService.deleteStudentProfile(1L);

        verify(studentProfileRepository, times(1)).delete(profile);
        verify(userRepository, times(1)).save(user);
    }

    // === Non-Student Profile Tests ===

    @Test
    void createNonStudentProfile_shouldEncryptSensitiveData() {
        User user = new User();
        user.setId("123");

        NonStudentProfile profile = new NonStudentProfile();
        profile.setTaxNumberEncrypted("123456");
        profile.setGsisNumberEncrypted("654321");
        profile.setPhilHealthNumberEncrypted("111111");
        profile.setPagIbigNumberEncrypted("222222");

        AppointmentRecord appointment = new AppointmentRecord();
        appointment.setPosition(Position.TEACHER_I);
        List<AppointmentRecord> appointments = new ArrayList<>();
        appointments.add(appointment);
        profile.setEmploymentAppointments(appointments);

        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
        when(nonStudentProfileRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        NonStudentProfile created = profileService.createNonStudentProfile(user.getId(), profile);

        assertNotNull(created.getTaxNumberEncrypted());
        assertNotNull(created.getEmploymentAppointments().get(0).getSalaryGrade());
    }

    @Test
    void createNonStudentProfile_userNotFound_shouldThrow() {
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () ->
                profileService.createNonStudentProfile("badId", new NonStudentProfile())
        );
    }

    @Test
    void updateNonStudentProfile_shouldEncryptUpdatedFields() {
        NonStudentProfile existing = new NonStudentProfile();
        existing.setId(1L);

        when(nonStudentProfileRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(nonStudentProfileRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        NonStudentProfile updated = new NonStudentProfile();
        updated.setTaxNumberEncrypted("333333");

        AppointmentRecord record = new AppointmentRecord();
        record.setPosition(Position.TEACHER_I);
        updated.setEmploymentAppointments(List.of(record));

        NonStudentProfile result = profileService.updateNonStudentProfile(1L, updated);
        assertNotNull(result.getTaxNumberEncrypted());
    }

    @Test
    void deleteNonStudentProfile_shouldUnlinkAndDelete() {
        NonStudentProfile profile = new NonStudentProfile();
        User user = new User();
        profile.setLinkedUser(user);

        when(nonStudentProfileRepository.findById(1L)).thenReturn(Optional.of(profile));

        profileService.deleteNonStudentProfile(1L);

        verify(nonStudentProfileRepository, times(1)).delete(profile);
        verify(userRepository, times(1)).save(user);
    }

    // === Get Profile Tests ===

    @Test
    void getStudentProfileById_success() {
        StudentProfile profile = new StudentProfile();
        when(studentProfileRepository.findById(1L)).thenReturn(Optional.of(profile));
        assertThat(profileService.getStudentProfileById(1L)).isEqualTo(profile);
    }

    @Test
    void getStudentProfileById_notFound_shouldThrow() {
        when(studentProfileRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> profileService.getStudentProfileById(1L));
    }

    @Test
    void getNonStudentProfileById_success() {
        NonStudentProfile profile = new NonStudentProfile();
        when(nonStudentProfileRepository.findById(1L)).thenReturn(Optional.of(profile));
        assertThat(profileService.getNonStudentProfileById(1L)).isEqualTo(profile);
    }

    @Test
    void getNonStudentProfileById_notFound_shouldThrow() {
        when(nonStudentProfileRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> profileService.getNonStudentProfileById(1L));
    }

    @Test
    void getProfileByUserId_studentFound() {
        StudentProfile studentProfile = new StudentProfile();
        User user = new User();
        user.setId("123");
        studentProfile.setLinkedUser(user);

        when(studentProfileRepository.findAll()).thenReturn(List.of(studentProfile));

        Object result = profileService.getProfileByUserId("123");
        assertThat(result).isInstanceOf(StudentProfile.class);
    }

    @Test
    void getProfileByUserId_nonStudentFound() {
        when(studentProfileRepository.findAll()).thenReturn(new ArrayList<>());
        NonStudentProfile nonStudentProfile = new NonStudentProfile();
        User user = new User();
        user.setId("123");
        nonStudentProfile.setLinkedUser(user);
        when(nonStudentProfileRepository.findAll()).thenReturn(List.of(nonStudentProfile));

        Object result = profileService.getProfileByUserId("123");
        assertThat(result).isInstanceOf(NonStudentProfile.class);
    }


    @Test
    void getAllProfiles_success() {
        when(studentProfileRepository.findAll()).thenReturn(List.of(new StudentProfile()));
        when(nonStudentProfileRepository.findAll()).thenReturn(List.of(new NonStudentProfile()));

        List<Object> result = profileService.getAllProfiles();
        assertThat(result).hasSize(2);
    }

    @Test
    void createStudentProfile_shouldThrowIfStudentProfileAlreadyExists() {
        User user = new User();
        user.setId("user123");
        user.setStudentProfile(new StudentProfile());

        when(userRepository.findById("user123")).thenReturn(Optional.of(user));

        StudentProfile studentProfile = new StudentProfile();

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                profileService.createStudentProfile("user123", studentProfile));

        assertEquals("StudentProfile already exists for this user.", exception.getMessage());
    }

    @Test
    void createStudentProfile_shouldCalculateGradeStatusBasedOnGpa() {
        User user = new User();
        user.setId("user123");

        when(userRepository.findById("user123")).thenReturn(Optional.of(user));

        StudentProfile studentProfile = new StudentProfile();
        studentProfile.setFirstName("Test");
        studentProfile.setLastName("Student");

        SchoolYearHistory history = new SchoolYearHistory();
        history.setGpa(96f); // note: Float
        studentProfile.setSchoolHistories(List.of(history));

        StudentProfile result = profileService.createStudentProfile("user123", studentProfile);

        assertEquals(StudentGradeStatus.WITH_HIGH_HONORS, result.getSchoolHistories().get(0).getGradeStatus());
    }

    @Test
    void updateStudentProfile_shouldUpdateGradeStatusForSchoolHistory() {
        StudentProfile existingProfile = new StudentProfile();
        existingProfile.setId(1L);

        when(studentProfileRepository.findById(1L)).thenReturn(Optional.of(existingProfile));
        when(studentProfileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        SchoolYearHistory history = new SchoolYearHistory();
        history.setGpa(96f);

        StudentProfile updatedProfile = new StudentProfile();
        updatedProfile.setFirstName("Test");
        updatedProfile.setSchoolHistories(List.of(history));

        StudentProfile result = profileService.updateStudentProfile(1L, updatedProfile);

        assertNotNull(result);
        assertEquals(1, result.getSchoolHistories().size());
        assertEquals(StudentGradeStatus.WITH_HIGH_HONORS, result.getSchoolHistories().get(0).getGradeStatus());
    }

    @Test
    void calculateGradeStatus_shouldReturnCorrectStatus() {
        assertEquals(StudentGradeStatus.FAILED, invokeCalculateGradeStatus(null));
        assertEquals(StudentGradeStatus.WITH_HIGHEST_HONORS, invokeCalculateGradeStatus(99f));
        assertEquals(StudentGradeStatus.WITH_HIGH_HONORS, invokeCalculateGradeStatus(96f));
        assertEquals(StudentGradeStatus.WITH_HONORS, invokeCalculateGradeStatus(91f));
        assertEquals(StudentGradeStatus.PASSED, invokeCalculateGradeStatus(76f));
        assertEquals(StudentGradeStatus.FAILED, invokeCalculateGradeStatus(70f));
    }

    // ←–– Changed to Float (instead of Double) to match ProfileService.calculateGradeStatus(Float)
    private StudentGradeStatus invokeCalculateGradeStatus(Float gpa) {
        try {
            var method = ProfileService.class.getDeclaredMethod("calculateGradeStatus", Float.class);
            method.setAccessible(true);
            return (StudentGradeStatus) method.invoke(profileService, gpa);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void updateStudentProfile_shouldUpdateBmiCategoryCorrectly() {
        StudentProfile existingProfile = new StudentProfile();
        existingProfile.setId(1L);

        when(studentProfileRepository.findById(1L)).thenReturn(Optional.of(existingProfile));
        when(studentProfileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        NutritionalStatus nutrition = new NutritionalStatus();
        nutrition.setHeightInCentimeters(1.6f);
        nutrition.setWeightInKilograms(40f);

        StudentProfile updatedProfile = new StudentProfile();
        updatedProfile.setFirstName("Test");
        updatedProfile.setNutritionalStatus(nutrition);

        StudentProfile result = profileService.updateStudentProfile(1L, updatedProfile);

        assertNotNull(result);
        assertEquals(BMICategory.OBESE, result.getNutritionalStatus().getBmiCategory());
    }

    @Test
    void createNonStudentProfile_shouldThrowIfProfileAlreadyExists() {
        User user = new User();
        user.setId("user123");
        user.setNonStudentProfile(new NonStudentProfile());

        when(userRepository.findById("user123")).thenReturn(Optional.of(user));

        NonStudentProfile profile = new NonStudentProfile();

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                profileService.createNonStudentProfile("user123", profile));

        assertEquals("NonStudentProfile already exists for this user.", exception.getMessage());
    }

    @Test
    void createNonStudentProfile_shouldEncryptSensitiveFields() {
        User user = new User();
        user.setId("user123");

        when(userRepository.findById("user123")).thenReturn(Optional.of(user));

        NonStudentProfile profile = new NonStudentProfile();
        profile.setTaxNumberEncrypted("plainTax");
        profile.setGsisNumberEncrypted("plainGsis");
        profile.setPhilHealthNumberEncrypted("plainPhilHealth");
        profile.setPagIbigNumberEncrypted("plainPagIbig");

        NonStudentProfile result = profileService.createNonStudentProfile("user123", profile);

        assertNotNull(result.getTaxNumberEncrypted());
        assertNotNull(result.getGsisNumberEncrypted());
        assertNotNull(result.getPhilHealthNumberEncrypted());
        assertNotNull(result.getPagIbigNumberEncrypted());
    }

    @Test
    void updateStudentProfile_shouldUpdateBmiCategoryToObese() {
        StudentProfile existingProfile = new StudentProfile();
        existingProfile.setId(3L);

        when(studentProfileRepository.findById(3L)).thenReturn(Optional.of(existingProfile));
        when(studentProfileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        NutritionalStatus nutrition = new NutritionalStatus();
        nutrition.setHeightInCentimeters(1.6f);
        nutrition.setWeightInKilograms(90f);

        StudentProfile updatedProfile = new StudentProfile();
        updatedProfile.setFirstName("Test");
        updatedProfile.setNutritionalStatus(nutrition);

        StudentProfile result = profileService.updateStudentProfile(3L, updatedProfile);

        assertNotNull(result);
        assertEquals(BMICategory.OBESE, result.getNutritionalStatus().getBmiCategory());
    }


    @Test
    void updateNonStudentProfile_shouldUpdateDependentChildAgeCorrectly() {
        NonStudentProfile existingProfile = new NonStudentProfile();
        existingProfile.setId(1L);
        existingProfile.setDependentChildren(new ArrayList<>());

        DependentChild child = new DependentChild();
        child.setBirthDate(LocalDate.now().minusYears(12));
        existingProfile.getDependentChildren().add(child);

        when(nonStudentProfileRepository.findById(1L)).thenReturn(Optional.of(existingProfile));
        when(nonStudentProfileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        NonStudentProfile updatedData = new NonStudentProfile();
        updatedData.setDependentChildren(List.of(child));

        NonStudentProfile result = profileService.updateNonStudentProfile(1L, updatedData);

        assertNotNull(result.getDependentChildren());
        assertEquals(12, result.getDependentChildren().get(0).getAge());
    }

    @Test
    void updateNonStudentProfile_shouldAssignSalaryGradeBasedOnPosition() {
        NonStudentProfile existingProfile = new NonStudentProfile();
        existingProfile.setId(1L);

        AppointmentRecord record = new AppointmentRecord();
        record.setPosition(Position.TEACHER_I);

        NonStudentProfile updatedData = new NonStudentProfile();
        updatedData.setEmploymentAppointments(List.of(record));

        when(nonStudentProfileRepository.findById(1L)).thenReturn(Optional.of(existingProfile));
        when(nonStudentProfileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        NonStudentProfile result = profileService.updateNonStudentProfile(1L, updatedData);

        assertNotNull(result.getEmploymentAppointments().get(0).getSalaryGrade());
        assertTrue(result.getEmploymentAppointments().get(0).getSalaryGrade() > 0);
    }

    @Test
    void updateNonStudentProfile_shouldHandleInvalidSalaryGradeGracefully() {
        Position invalidPosition = mock(Position.class);
        when(invalidPosition.name()).thenReturn("NOT_IN_SALARYGRADE");

        AppointmentRecord record = new AppointmentRecord();
        record.setPosition(invalidPosition);

        NonStudentProfile input = new NonStudentProfile();
        input.setEmploymentAppointments(List.of(record));
        input.setDependentChildren(new ArrayList<>());
        input.setLinkedUser(new User());

        NonStudentProfile existing = new NonStudentProfile();
        existing.setEmploymentAppointments(List.of(record));
        existing.setDependentChildren(new ArrayList<>());

        when(nonStudentProfileRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(nonStudentProfileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        NonStudentProfile result = profileService.updateNonStudentProfile(1L, input);

        assertEquals(0, result.getEmploymentAppointments().get(0).getSalaryGrade());
    }

    @Test
    void updateNonStudentProfile_shouldUpdateDependentChildAgesBasedOnBirthDate() {
        NonStudentProfile existingProfile = new NonStudentProfile();
        existingProfile.setId(1L);
        existingProfile.setDependentChildren(new ArrayList<>());

        DependentChild child = new DependentChild();
        child.setBirthDate(LocalDate.now().minusYears(8));
        existingProfile.getDependentChildren().add(child);

        when(nonStudentProfileRepository.findById(1L)).thenReturn(Optional.of(existingProfile));
        when(nonStudentProfileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        NonStudentProfile updatedProfile = new NonStudentProfile();
        updatedProfile.setDependentChildren(List.of(child));

        NonStudentProfile result = profileService.updateNonStudentProfile(1L, updatedProfile);

        assertNotNull(result);
        assertEquals(8, result.getDependentChildren().get(0).getAge());
    }

    @Test
    void updateStudentProfile_shouldUpdateParentFieldsAndId() {
        StudentProfile existingProfile = new StudentProfile();
        existingProfile.setId(1L);

        when(studentProfileRepository.findById(1L)).thenReturn(Optional.of(existingProfile));
        when(studentProfileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        StudentProfile updatedData = new StudentProfile();
        updatedData.setMotherFirstName("Jane");
        updatedData.setMotherMiddleName("M.");
        updatedData.setMotherMaidenName("Smith");
        updatedData.setFatherFirstName("John");
        updatedData.setFatherMiddleName("A.");
        updatedData.setFatherLastName("Doe");

        StudentProfile result = profileService.updateStudentProfile(1L, updatedData);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Jane", result.getMotherFirstName());
        assertEquals("M.", result.getMotherMiddleName());
        assertEquals("Smith", result.getMotherMaidenName());
        assertEquals("John", result.getFatherFirstName());
        assertEquals("A.", result.getFatherMiddleName());
        assertEquals("Doe", result.getFatherLastName());
    }

    @Test
    void updateNonStudentProfile_shouldUpdateAddressAndId() {
        NonStudentProfile existingProfile = new NonStudentProfile();
        existingProfile.setId(2L);

        when(nonStudentProfileRepository.findById(2L)).thenReturn(Optional.of(existingProfile));
        when(nonStudentProfileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        NonStudentProfile updatedData = new NonStudentProfile();
        Address address = new Address();
        address.setStreetAddress("123 Main St");
        address.setCityMunicipality("Springfield");
        address.setProvinceState("Illinois");
        address.setCountry("USA");
        address.setZipCode("62704");
        updatedData.setAddress(address);

        NonStudentProfile result = profileService.updateNonStudentProfile(2L, updatedData);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("123 Main St", result.getAddress().getStreetAddress());
        assertEquals("Springfield", result.getAddress().getCityMunicipality());
        assertEquals("Illinois", result.getAddress().getProvinceState());
        assertEquals("USA", result.getAddress().getCountry());
        assertEquals("62704", result.getAddress().getZipCode());
    }

    @Test
    void updateStudentProfile_shouldSetAllFieldsCorrectly() {
        Address address = new Address();
        address.setStreetAddress("123 Main");
        address.setSubdivision("Sunny Hills");
        address.setCityMunicipality("Metro City");
        address.setProvinceState("Central");
        address.setCountry("PH");
        address.setZipCode("1000");

        PhoneNumberEntry phone = new PhoneNumberEntry();
        phone.setType(PhoneType.CELLULAR);
        phone.setNumber("09123456789");

        SchoolYearHistory history = new SchoolYearHistory();
        history.setSchoolName("Green HS");
        history.setSchoolAddress(address);
        history.setGradeLevel("10");
        history.setSchoolYearStart(LocalDate.of(2022, 6, 1));
        history.setSchoolYearEnd(LocalDate.of(2023, 3, 15));
        history.setSectionNicknames("A");
        history.setCompleted(true);
        history.setGpa(94f);

        NutritionalStatus nutrition = new NutritionalStatus();
        nutrition.setHeightInCentimeters(1.6f);
        nutrition.setWeightInKilograms(50f);

        StudentProfile student = new StudentProfile();
        student.setFirstName("Anna");
        student.setMiddleName("M");
        student.setLastName("Rivera");
        student.setBirthDate(LocalDate.of(2005, 2, 10));
        student.setReligion("Catholic");
        student.setDialects(List.of("Tagalog", "Bisaya"));
        student.setPhoneNumbers(List.of(phone));
        student.setAddress(address);
        student.setBirthPlace(address);
        student.setSchoolHistories(List.of(history));
        student.setNutritionalStatus(nutrition);
        student.setGradeLevel("10");
        student.setFirstAttendanceDate(LocalDate.of(2021, 6, 10));
        student.setProfilePicture("photo.jpg");
        student.setMotherFirstName("Maria");
        student.setMotherMiddleName("Lopez");
        student.setMotherMaidenName("Santos");
        student.setFatherFirstName("Juan");
        student.setFatherMiddleName("Cruz");
        student.setFatherLastName("Rivera");
        student.setLinkedUser(new User());

        when(studentProfileRepository.findById(anyLong())).thenReturn(Optional.of(student));
        when(studentProfileRepository.save(any())).thenReturn(student);

        StudentProfile result = profileService.updateStudentProfile(1L, student);

        assertEquals("Anna", result.getFirstName());
        assertEquals("Lopez", result.getMotherMiddleName());
        assertEquals("Rivera", result.getFatherLastName());
        assertEquals(BMICategory.OBESE, result.getNutritionalStatus().getBmiCategory());
        assertEquals("Green HS", result.getSchoolHistories().get(0).getSchoolName());
        assertEquals("10", result.getSchoolHistories().get(0).getGradeLevel());
        assertEquals(List.of("Tagalog", "Bisaya"), result.getDialects());
    }

    @Test
    void updateNonStudentProfile_shouldSetAllFieldsCorrectly() {
        Address address = new Address();
        address.setStreetAddress("123 Main");
        address.setSubdivision("Green Sub");
        address.setCityMunicipality("City");
        address.setProvinceState("State");
        address.setCountry("PH");
        address.setZipCode("1234");

        PhoneNumberEntry phone = new PhoneNumberEntry();
        phone.setType(PhoneType.HOME);
        phone.setNumber("555-1234");

        EducationRecord edu = new EducationRecord();
        edu.setSchoolName("Uni");
        edu.setEducationLevel("Bachelor");
        edu.setYearGraduated(2015);
        edu.setSchoolAddress(address);

        DependentChild child = new DependentChild();
        child.setFirstName("Leo");
        child.setBirthDate(LocalDate.now().minusYears(8));

        AppointmentRecord appointment = new AppointmentRecord();
        appointment.setPosition(Position.TEACHER_I);
        appointment.setDateIssued(LocalDate.of(2020, 1, 1));
        appointment.setAppointmentType(AppointmentType.FIFTH_APPOINTMENT);
        appointment.setSalaryAmount(26000);

        NonStudentProfile nonStudent = new NonStudentProfile();
        nonStudent.setFirstName("Carlos");
        nonStudent.setMiddleName("M");
        nonStudent.setLastName("Santos");
        nonStudent.setBirthDate(LocalDate.of(1980, 5, 20));
        nonStudent.setGender("Male");
        nonStudent.setCivilStatus(CivilStatus.SINGLE);
        nonStudent.setPhoneNumbers(List.of(phone));
        nonStudent.setSpouseFirstName("Anna");
        nonStudent.setSpouseMiddleName("C.");
        nonStudent.setSpouseLastName("Santos");
        nonStudent.setSpouseOccupation("Nurse");
        nonStudent.setDependentChildren(List.of(child));
        nonStudent.setEducationalBackground(List.of(edu));
        nonStudent.setEmploymentAppointments(List.of(appointment));
        nonStudent.setDepartmentOfEducationEmail("carlos@deped.gov.ph");
        nonStudent.setProfilePicture("pic.png");
        nonStudent.setGradeLevel("N/A");
        nonStudent.setAddress(address);
        nonStudent.setBirthPlace(address);
        nonStudent.setGsisNumberEncrypted("123");
        nonStudent.setPhilHealthNumberEncrypted("456");
        nonStudent.setPagIbigNumberEncrypted("789");
        nonStudent.setLinkedUser(new User());

        when(nonStudentProfileRepository.findById(anyLong())).thenReturn(Optional.of(nonStudent));
        when(nonStudentProfileRepository.save(any())).thenReturn(nonStudent);

        NonStudentProfile result = profileService.updateNonStudentProfile(1L, nonStudent);

        assertEquals("Carlos", result.getFirstName());
        assertEquals("Green Sub", result.getAddress().getSubdivision());
        assertEquals("Bachelor", result.getEducationalBackground().get(0).getEducationLevel());
        assertEquals(8, result.getDependentChildren().get(0).getAge());
        assertEquals(Position.TEACHER_I, result.getEmploymentAppointments().get(0).getPosition());
        assertTrue(result.getGsisNumberEncrypted().length() > 0);
        assertTrue(result.getPhilHealthNumberEncrypted().length() > 0);
    }

    @Test
    void updateProfiles_shouldCoverAllRemainingFields() {
        // === Grade → ScoreDetails embedding ===
        Grade grade = new Grade();
        ScoreDetails details = new ScoreDetails();
        details.getPerformanceScores().addAll(List.of(85, 90));
        details.getQuizScores().addAll(List.of(88, 92));
        details.getQuarterlyExamScores().addAll(List.of(87));
        details.recalcTotals(); // so totals, ps, ws get calculated
        grade.setScoreDetails(details);

        assertEquals(2, grade.getScoreDetails().getPerformanceScores().size());
        assertEquals(2, grade.getScoreDetails().getQuizScores().size());
        assertEquals(1, grade.getScoreDetails().getQuarterlyExamScores().size());
        assertEquals(175, grade.getScoreDetails().getPerformanceTotal());
        assertEquals(180, grade.getScoreDetails().getQuizTotal());
        assertEquals(87, grade.getScoreDetails().getQuarterlyExamTotal());

        // === EducationRecord ===
        Address eduAddress = new Address();
        eduAddress.setStreetAddress("123 College Ave");

        EducationRecord education = new EducationRecord();
        education.setSchoolName("ABC College");
        education.setSchoolAddress(eduAddress);
        education.setEducationLevel("Bachelor's Degree");
        education.setYearGraduated(2021);

        assertEquals("ABC College", education.getSchoolName());
        assertEquals("123 College Ave", education.getSchoolAddress().getStreetAddress());
        assertEquals(2021, education.getYearGraduated());

        // === PhoneNumberEntry ===
        PhoneNumberEntry phone = new PhoneNumberEntry();
        phone.setType(PhoneType.CELLULAR);
        phone.setNumber("09171234567");

        assertEquals(PhoneType.CELLULAR, phone.getType());
        assertEquals("09171234567", phone.getNumber());

        // === SchoolYearHistory ===
        SchoolYearHistory history = new SchoolYearHistory();
        history.setSchoolAddress(eduAddress);
        history.setSchoolYearStart(LocalDate.of(2020, 6, 1));
        history.setSchoolYearEnd(LocalDate.of(2021, 3, 30));
        history.setSectionNicknames("Red");
        history.setCompleted(true);

        assertEquals("123 College Ave", history.getSchoolAddress().getStreetAddress());
        assertEquals(LocalDate.of(2020, 6, 1), history.getSchoolYearStart());
        assertEquals(LocalDate.of(2021, 3, 30), history.getSchoolYearEnd());
        assertEquals("Red", history.getSectionNicknames());
        assertTrue(history.isCompleted());

        // === DependentChild ===
        DependentChild child = new DependentChild();
        child.setFirstName("Ella");
        child.setMiddleName("Marie");
        child.setLastName("Cruz");

        assertEquals("Ella", child.getFirstName());
        assertEquals("Marie", child.getMiddleName());
        assertEquals("Cruz", child.getLastName());

        // === AppointmentRecord ===
        AppointmentRecord appointment = new AppointmentRecord();
        appointment.setAppointmentType(AppointmentType.FIFTH_APPOINTMENT);
        appointment.setDateIssued(LocalDate.of(2022, 1, 15));
        appointment.setPosition(Position.TEACHER_I);
        appointment.setSalaryAmount(26000);

        assertEquals(AppointmentType.FIFTH_APPOINTMENT, appointment.getAppointmentType());
        assertEquals(LocalDate.of(2022, 1, 15), appointment.getDateIssued());
        assertEquals(Position.TEACHER_I, appointment.getPosition());
        assertEquals(26000, appointment.getSalaryAmount());
    }

    @Test
    void position_shouldReturnCorrectSalaryGrade() {
        assertEquals(SalaryGrade.TEACHER_I, Position.TEACHER_I.getSalaryGrade());
        assertEquals(SalaryGrade.HEAD_TEACHER_IV, Position.HEAD_TEACHER_IV.getSalaryGrade());
        assertEquals(SalaryGrade.SCHOOL_PRINCIPAL_I, Position.SCHOOL_PRINCIPAL_I.getSalaryGrade());
    }

}
