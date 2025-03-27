package medical_clinics.user_account.services;

import jakarta.transaction.Transactional;
import medical_clinics.patient.exceptions.PatientNotFoundException;
import medical_clinics.patient.model.Patient;
import medical_clinics.patient.repository.PatientRepository;
import medical_clinics.physician.exceptions.PhysicianAlreadyExistException;
import medical_clinics.physician.model.Physician;
import medical_clinics.physician.repository.PhysicianRepository;
import medical_clinics.shared.exception.PersonalInformationDontMatchException;
import medical_clinics.specialty.model.Specialty;
import medical_clinics.specialty.model.SpecialtyName;
import medical_clinics.specialty.repository.SpecialtyRepository;
import medical_clinics.user_account.exceptions.UserAlreadyExistsException;
import medical_clinics.user_account.model.Role;
import medical_clinics.user_account.model.UserAccount;
import medical_clinics.user_account.model.UserStatus;
import medical_clinics.user_account.property.UserProperty;
import medical_clinics.user_account.repository.UserAccountRepository;
import medical_clinics.user_account.service.UserAccountService;
import medical_clinics.web.dto.RegisterRequest;
import medical_clinics.web.dto.UserAccountEditRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class UserAccountServiceITests {
    private static final String EMAIL = "test@email.com";
    private static final String FIRSTNAME = "testfirstname";
    private static final String LASTNAME = "testlastname";
    private static final String PASSWORD = "!1aAAAAAAA";

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PhysicianRepository physicianRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Autowired
    private UserProperty property;

    @Test
    @Transactional
    void when_blockUserAccount_StatusChangeToBlocked () {
        UserAccount userAccount = UserAccount.builder ( )
                .email ( EMAIL )
                .role ( Role.ADMIN )
                .password ( PASSWORD )
                .status ( UserStatus.ACTIVE )
                .build ( );

        UUID accountId = userAccountRepository.save ( userAccount ).getId ( );
        userAccountService.blockUserAccount ( accountId );

        assertEquals ( UserStatus.BLOCKED, userAccountRepository.findById ( accountId ).get ( ).getStatus ( ) );
    }

    @Test
    @Transactional
    void when_deleteUserAccount_StatusChangeToInactive () {
        UserAccount userAccount = UserAccount.builder ( )
                .email ( EMAIL )
                .role ( Role.ADMIN )
                .password ( PASSWORD )
                .status ( UserStatus.ACTIVE )
                .build ( );

        UUID accountId = userAccountRepository.save ( userAccount ).getId ( );
        userAccountService.deleteUserAccount ( accountId );

        assertEquals ( UserStatus.INACTIVE, userAccountRepository.findById ( accountId ).get ( ).getStatus ( ) );
    }

    @Test
    void when_register_withNewUser_then_registerSucceeds () {

        RegisterRequest registerRequest = RegisterRequest.builder ( )
                .email ( EMAIL )
                .firstName ( FIRSTNAME )
                .lastName ( LASTNAME )
                .password ( PASSWORD )
                .build ( );

        userAccountService.register ( registerRequest );

        assertTrue ( userAccountRepository.findByEmail ( EMAIL ).isPresent ( ) );
        assertTrue ( patientRepository.findByEmail ( EMAIL ).isPresent ( ) );
    }

    @Test
    void when_register_withExistingUser_thenThrowsException () {
        UserAccount userAccount = UserAccount.builder ( )
                .email ( EMAIL )
                .role ( Role.ADMIN )
                .password ( PASSWORD )
                .status ( UserStatus.ACTIVE )
                .build ( );

        userAccountRepository.save ( userAccount );

        RegisterRequest registerRequest = RegisterRequest.builder ( )
                .email ( EMAIL )
                .firstName ( FIRSTNAME )
                .lastName ( LASTNAME )
                .password ( PASSWORD )
                .build ( );

        assertThrows ( UserAlreadyExistsException.class, () -> userAccountService.register ( registerRequest ) );
    }

    @Test
    @Transactional
    void when_when_register_withExistingPatient_thenSetPatientAccount () {
        Patient patient = Patient.builder ( )
                .email ( EMAIL )
                .firstName ( FIRSTNAME )
                .lastName ( LASTNAME )
                .build ( );

        patientRepository.save ( patient );

        RegisterRequest registerRequest = RegisterRequest.builder ( )
                .email ( EMAIL )
                .firstName ( FIRSTNAME )
                .lastName ( LASTNAME )
                .password ( PASSWORD )
                .phone ( "123456789" )
                .build ( );

        userAccountService.register ( registerRequest );

        Patient patientAfter = patientRepository.findByEmail ( EMAIL ).get ( );

        assertEquals ( "123456789", patientAfter.getPhone ( ) );
        assertSame ( patientAfter.getUserAccount ( ), userAccountRepository.findByEmail ( EMAIL ).get ( ) );
    }

    @Test
    @Transactional
    void when_when_register_withExistingPatientPhone_thenSetPatientAccount () {
        String phone = "123456789";
        Patient patient = Patient.builder ( )
                .firstName ( FIRSTNAME )
                .lastName ( LASTNAME )
                .phone ( phone )
                .build ( );

        patientRepository.save ( patient );

        RegisterRequest registerRequest = RegisterRequest.builder ( )
                .email ( EMAIL )
                .firstName ( FIRSTNAME )
                .lastName ( LASTNAME )
                .password ( PASSWORD )
                .phone ( phone )
                .build ( );

        userAccountService.register ( registerRequest );

        Patient patientAfter = patientRepository.findByPhone ( phone ).get ( );

        assertEquals ( EMAIL, patientAfter.getEmail ( ) );
        assertSame ( patientAfter.getUserAccount ( ), userAccountRepository.findByEmail ( EMAIL ).get ( ) );
    }

    @Test
    @Transactional
    void when_when_register_withExistingPhysician_thenSetPhysicianAccount () {
        Specialty specialty = specialtyRepository.getByName ( SpecialtyName.ALLERGIST ).get ( );

        Physician physician = Physician.builder ( )
                .email ( EMAIL )
                .firstName ( FIRSTNAME )
                .lastName ( LASTNAME )
                .identificationNumber ( "A11111111111" )
                .specialty ( specialty )
                .build ( );

        physicianRepository.save ( physician );

        RegisterRequest registerRequest = RegisterRequest.builder ( )
                .email ( EMAIL )
                .firstName ( FIRSTNAME )
                .lastName ( LASTNAME )
                .password ( PASSWORD )
                .build ( );

        userAccountService.register ( registerRequest );

        Patient patient = patientRepository.findByEmail ( EMAIL ).get ( );
        Physician physicianAfter = physicianRepository.findByEmail ( EMAIL ).get ( );

        assertEquals ( Role.PHYSICIAN, physicianAfter.getUserAccount ( ).getRole ( ) );
        assertSame ( patient.getUserAccount ( ), userAccountRepository.findByEmail ( EMAIL ).get ( ) );
        assertSame ( physicianAfter.getUserAccount ( ), userAccountRepository.findByEmail ( EMAIL ).get ( ) );
    }

    @Test
    @Transactional
    void when_when_register_withExistingPatientConflictPhone_thenThrowsException () {
        Patient patient = Patient.builder ( )
                .email ( "Some@mail.com" )
                .firstName ( "Other" )
                .lastName ( "Name" )
                .phone ( "123456789" )
                .build ( );

        patientRepository.save ( patient );

        RegisterRequest registerRequest = RegisterRequest.builder ( )
                .email ( EMAIL )
                .firstName ( FIRSTNAME )
                .lastName ( LASTNAME )
                .password ( PASSWORD )
                .phone ( "123456789" )
                .build ( );

        assertThrows ( PersonalInformationDontMatchException.class,
                () -> userAccountService.register ( registerRequest ) );
    }

    @Test
    @Transactional
    void when_when_register_withExistingPatientConflictEmail_thenThrowsException () {
        Patient patient = Patient.builder ( )
                .email ( EMAIL )
                .firstName ( "Other" )
                .lastName ( "Name" )
                .phone ( "1234567891" )
                .build ( );

        patientRepository.save ( patient );

        RegisterRequest registerRequest = RegisterRequest.builder ( )
                .email ( EMAIL )
                .firstName ( FIRSTNAME )
                .lastName ( LASTNAME )
                .password ( PASSWORD )
                .phone ( "123456789" )
                .build ( );

        assertThrows ( PersonalInformationDontMatchException.class,
                () -> userAccountService.register ( registerRequest ) );
    }

    @Test
    @Transactional
    void when_when_register_withExistingPatientsConflict_thenThrowsException () {
        Patient patient = Patient.builder ( )
                .email ( "other@mail.com" )
                .firstName ( "Other" )
                .lastName ( "Name" )
                .phone ( "123456789" )
                .build ( );

        Patient patient2 = Patient.builder ( )
                .email ( EMAIL )
                .firstName ( "Other" )
                .lastName ( "Name" )
                .phone ( "1234567891" )
                .build ( );

        patientRepository.save ( patient );
        patientRepository.save ( patient2 );

        RegisterRequest registerRequest = RegisterRequest.builder ( )
                .email ( EMAIL )
                .firstName ( FIRSTNAME )
                .lastName ( LASTNAME )
                .password ( PASSWORD )
                .phone ( "123456789" )
                .build ( );

        assertThrows ( PersonalInformationDontMatchException.class,
                () -> userAccountService.register ( registerRequest ) );
    }

    @Test
    @Transactional
    void when_when_register_withExistingPatientHaveAccount_thenThrowsException () {
        UserAccount account = userAccountRepository.save (
                UserAccount.builder ( )
                        .email ( "Some@mail.com" )
                        .password ( PASSWORD )
                        .status ( UserStatus.ACTIVE )
                        .role ( Role.ADMIN )
                        .build ( )
        );

        Patient patient = Patient.builder ( )
                .email ( "Some@mail.com" )
                .firstName ( FIRSTNAME )
                .lastName ( LASTNAME )
                .phone ( "123456789" )
                .userAccount ( account )
                .build ( );

        patientRepository.save ( patient );

        RegisterRequest registerRequest = RegisterRequest.builder ( )
                .email ( EMAIL )
                .firstName ( FIRSTNAME )
                .lastName ( LASTNAME )
                .password ( PASSWORD )
                .phone ( "123456789" )
                .build ( );

        assertThrows ( UserAlreadyExistsException.class,
                () -> userAccountService.register ( registerRequest ) );
    }

    @Test
    @Transactional
    void when_when_register_withExistingPhysicianConflictEmail_thenThrowsException () {
        Specialty specialty = specialtyRepository.getByName ( SpecialtyName.ALLERGIST ).get ( );

        Physician physician = Physician.builder ( )
                .email ( EMAIL )
                .firstName ( "Other" )
                .lastName ( "Name" )
                .identificationNumber ( "A11111111111" )
                .specialty ( specialty )
                .build ( );

        physicianRepository.save ( physician );

        RegisterRequest registerRequest = RegisterRequest.builder ( )
                .email ( EMAIL )
                .firstName ( FIRSTNAME )
                .lastName ( LASTNAME )
                .password ( PASSWORD )
                .build ( );

        assertThrows ( PersonalInformationDontMatchException.class,
                () -> userAccountService.register ( registerRequest ) );
    }

    @Test
    @Transactional
    void when_switchUserAccountRole_RoleDefault_thenExpectRoleAdmin () {
        UserAccount userAccount = UserAccount.builder ( )
                .email ( EMAIL )
                .role ( property.getDefaultRole ( ) )
                .password ( PASSWORD )
                .status ( UserStatus.ACTIVE )
                .build ( );

        UUID id = userAccountRepository.save ( userAccount ).getId ( );

        userAccountService.switchUserAccountRole ( id );

        assertEquals ( Role.ADMIN, userAccountRepository.findById ( id ).get ( ).getRole ( ) );
    }

    @Test
    void when_switchUserAccountRole_RoleAdmin_thenExpectRoleDefault () {
        UserAccount userAccount = UserAccount.builder ( )
                .email ( EMAIL )
                .role ( Role.ADMIN )
                .password ( PASSWORD )
                .status ( UserStatus.ACTIVE )
                .build ( );

        UUID id = userAccountRepository.save ( userAccount ).getId ( );

        userAccountService.switchUserAccountRole ( id );

        assertEquals ( property.getDefaultRole ( ), userAccountRepository.findById ( id ).get ( ).getRole ( ) );
    }

    @Test
    void when_switchUserAccountRole_RolePhysician_thenExpectRoleAdmin () {
        UserAccount userAccount = UserAccount.builder ( )
                .email ( EMAIL )
                .role ( Role.PHYSICIAN )
                .password ( PASSWORD )
                .status ( UserStatus.ACTIVE )
                .build ( );

        UUID id = userAccountRepository.save ( userAccount ).getId ( );

        userAccountService.switchUserAccountRole ( id );

        assertEquals ( Role.ADMIN, userAccountRepository.findById ( id ).get ( ).getRole ( ) );
    }

    @Test
    void when_switchUserAccountRole_RoleAdminAccountOfPhysician_thenExpectRolePhysician () {
        UserAccount userAccount = UserAccount.builder ( )
                .email ( EMAIL )
                .role ( Role.ADMIN )
                .password ( PASSWORD )
                .status ( UserStatus.ACTIVE )
                .build ( );

        UUID id = userAccountRepository.save ( userAccount ).getId ( );

        Specialty specialty = specialtyRepository.getByName ( SpecialtyName.ALLERGIST ).get ( );

        Physician physician = Physician.builder ( )
                .email ( EMAIL )
                .firstName ( "Other" )
                .lastName ( "Name" )
                .identificationNumber ( "A11111111111" )
                .specialty ( specialty )
                .userAccount ( userAccount )
                .build ( );

        physicianRepository.save ( physician );

        userAccountService.switchUserAccountRole ( id );

        assertEquals ( Role.PHYSICIAN, userAccountRepository.findById ( id ).get ( ).getRole ( ) );
    }

    @Test
    void when_editUserAccount_WithChangeEmailAndPassword_thenExpectChangeAccountData () {
        String newEmail = "new@email.com";
        String newPassword = "someOtherPassw0rd?";

        patientRepository.save (
                Patient.builder ( )
                        .email ( EMAIL )
                        .firstName ( FIRSTNAME )
                        .lastName ( LASTNAME )
                        .build ( )
        );

        UserAccount userAccount = UserAccount.builder ( )
                .email ( EMAIL )
                .role ( Role.ADMIN )
                .password ( PASSWORD )
                .status ( UserStatus.ACTIVE )
                .build ( );

        UUID id = userAccountRepository.save ( userAccount ).getId ( );
        String oldPasswordHash = userAccount.getPassword ( );

        UserAccountEditRequest editRequest = UserAccountEditRequest.builder ( )
                .id ( id )
                .email ( newEmail )
                .newPassword ( newPassword )
                .firstName ( FIRSTNAME )
                .lastName ( LASTNAME )
                .build ( );

        userAccountService.editUserAccount ( id, editRequest );

        UserAccount account = userAccountRepository.findById ( id ).get ( );

        assertEquals ( newEmail, account.getEmail ( ) );
        assertNotEquals ( newPassword, account.getPassword ( ) );
        assertNotEquals ( oldPasswordHash, account.getPassword ( ) );
    }

    @Test
    void when_editUserAccount_WithChangeOnlyNames_thenExpectChangePatientData () {
        String newName = "SomeOtherName";

        UUID patientId = patientRepository.save (
                Patient.builder ( )
                        .email ( EMAIL )
                        .firstName ( FIRSTNAME )
                        .lastName ( LASTNAME )
                        .build ( )
        ).getId ( );

        UserAccount userAccount = UserAccount.builder ( )
                .email ( EMAIL )
                .role ( Role.ADMIN )
                .password ( PASSWORD )
                .status ( UserStatus.ACTIVE )
                .build ( );

        UUID id = userAccountRepository.save ( userAccount ).getId ( );

        UserAccountEditRequest editRequest = UserAccountEditRequest.builder ( )
                .id ( id )
                .email ( EMAIL )
                .firstName ( newName )
                .lastName ( newName )
                .build ( );

        userAccountService.editUserAccount ( id, editRequest );

        Patient patient = patientRepository.findById ( patientId ).get ( );

        assertEquals ( newName, patient.getFirstName ( ) );
        assertEquals ( newName, patient.getLastName ( ) );
    }

    @Test
    void when_editUserAccount_WithChangeEmailWithNoPatientDataFound_thenExpectThrowException () {
        String newEmail = "new@email.com";

        UserAccount userAccount = UserAccount.builder ( )
                .email ( EMAIL )
                .role ( Role.ADMIN )
                .password ( PASSWORD )
                .status ( UserStatus.ACTIVE )
                .build ( );

        UUID id = userAccountRepository.save ( userAccount ).getId ( );

        UserAccountEditRequest editRequest = UserAccountEditRequest.builder ( )
                .id ( id )
                .email ( newEmail )
                .firstName ( FIRSTNAME )
                .lastName ( LASTNAME )
                .build ( );

        assertThrows ( PatientNotFoundException.class,
                () -> userAccountService.editUserAccount ( id, editRequest ) );
    }

    @Test
    void when_editUserAccount_WithChangesEveryWhere_thenExpectChange () {
        String newName = "SomeOtherName";
        String newEmail = "new@email.com";

        UUID patientId = patientRepository.save (
                Patient.builder ( )
                        .email ( EMAIL )
                        .firstName ( FIRSTNAME )
                        .lastName ( LASTNAME )
                        .build ( )
        ).getId ( );

        UserAccount userAccount = UserAccount.builder ( )
                .email ( EMAIL )
                .role ( Role.ADMIN )
                .password ( PASSWORD )
                .status ( UserStatus.ACTIVE )
                .build ( );

        UUID id = userAccountRepository.save ( userAccount ).getId ( );

        UserAccountEditRequest editRequest = UserAccountEditRequest.builder ( )
                .id ( id )
                .email ( newEmail )
                .firstName ( newName )
                .lastName ( newName )
                .phone ( "123456789" )
                .build ( );

        userAccountService.editUserAccount ( id, editRequest );

        Patient patient = patientRepository.findById ( patientId ).get ( );

        assertEquals ( newName, patient.getFirstName ( ) );
        assertEquals ( newName, patient.getLastName ( ) );
        assertEquals ( newEmail, patient.getEmail ( ) );
        assertEquals ( newEmail, userAccountRepository.findById ( id ).get ( ).getEmail ( ) );
        assertEquals ( "123456789", patient.getPhone ( ) );
    }

    @Test
    void when_editUserAccount_WithConflictInOtherPatientContacts_thenExpectThrowException () {
        String newName = "SomeOtherName";
        String newEmail = "new@email.com";
        String newPhone = "123456789";

        patientRepository.save (
                Patient.builder ( ).email ( newEmail ).firstName ( "Some" ).lastName ( "Else" ).phone ( newPhone ).build ( )
        );

        patientRepository.save (
                Patient.builder ( ).email ( EMAIL ).firstName ( FIRSTNAME ).lastName ( LASTNAME ).build ( )
        );

        UserAccount userAccount = UserAccount.builder ( )
                .email ( EMAIL )
                .role ( Role.ADMIN )
                .password ( PASSWORD )
                .status ( UserStatus.ACTIVE )
                .build ( );

        UUID id = userAccountRepository.save ( userAccount ).getId ( );

        UserAccountEditRequest editRequest = UserAccountEditRequest.builder ( )
                .id ( id )
                .email ( newEmail )
                .firstName ( newName )
                .lastName ( newName )
                .phone ( "123456789" )
                .build ( );

        assertThrows ( PersonalInformationDontMatchException.class,
                () -> userAccountService.editUserAccount ( id, editRequest ) );
    }

    @Test
    void when_editUserAccount_WithChangeNamesPhysician_thenExpectThrowException () {
        String newName = "SomeOtherName";
        Specialty specialty = specialtyRepository.getByName ( SpecialtyName.ALLERGIST ).get ( );

        physicianRepository.save (
                Physician.builder ( )
                        .email ( EMAIL )
                        .firstName ( FIRSTNAME )
                        .lastName ( LASTNAME )
                        .identificationNumber ( "A11111111" )
                        .specialty ( specialty )
                        .build ( )
        );

        patientRepository.save (
                Patient.builder ( ).email ( EMAIL ).firstName ( FIRSTNAME ).lastName ( LASTNAME ).build ( )
        );

        UserAccount userAccount = UserAccount.builder ( )
                .email ( EMAIL )
                .role ( Role.ADMIN )
                .password ( PASSWORD )
                .status ( UserStatus.ACTIVE )
                .build ( );

        UUID id = userAccountRepository.save ( userAccount ).getId ( );

        UserAccountEditRequest editRequest = UserAccountEditRequest.builder ( )
                .id ( id )
                .email ( EMAIL )
                .firstName ( newName )
                .lastName ( newName )
                .build ( );

        assertThrows ( PersonalInformationDontMatchException.class,
                () -> userAccountService.editUserAccount ( id, editRequest ) );
    }

    @Test
    void when_editUserAccount_WithConflictWithPhysicianEmail_thenExpectThrowException () {
        String newEmail = "new@email.com";
        Specialty specialty = specialtyRepository.getByName ( SpecialtyName.ALLERGIST ).get ( );

        physicianRepository.save (
                Physician.builder ( )
                        .email ( newEmail )
                        .firstName ( "Some" )
                        .lastName ( "One" )
                        .identificationNumber ( "A11111111" )
                        .specialty ( specialty )
                        .build ( )
        );

        patientRepository.save (
                Patient.builder ( ).email ( EMAIL ).firstName ( FIRSTNAME ).lastName ( LASTNAME ).build ( )
        );

        UserAccount userAccount = UserAccount.builder ( )
                .email ( EMAIL )
                .role ( Role.ADMIN )
                .password ( PASSWORD )
                .status ( UserStatus.ACTIVE )
                .build ( );

        UUID id = userAccountRepository.save ( userAccount ).getId ( );

        UserAccountEditRequest editRequest = UserAccountEditRequest.builder ( )
                .id ( id )
                .email ( newEmail )
                .firstName ( FIRSTNAME )
                .lastName ( LASTNAME )
                .build ( );

        assertThrows ( PhysicianAlreadyExistException.class,
                () -> userAccountService.editUserAccount ( id, editRequest ) );
    }

    @Test
    @Transactional
    void when_editUserAccount_withBelongToPhysician_thenPhysicianMaleChange () {
        String newEmail = "new@email.com";
        Specialty specialty = specialtyRepository.getByName ( SpecialtyName.ALLERGIST ).get ( );

        UUID physicianId = physicianRepository.save (
                Physician.builder ( )
                        .email ( EMAIL )
                        .firstName ( FIRSTNAME )
                        .lastName ( LASTNAME )
                        .identificationNumber ( "A11111111" )
                        .specialty ( specialty )
                        .build ( )
        ).getId ();

        patientRepository.save (
                Patient.builder ( ).email ( EMAIL ).firstName ( FIRSTNAME ).lastName ( LASTNAME ).build ( )
        );

        UserAccount userAccount = UserAccount.builder ( )
                .email ( EMAIL )
                .role ( Role.ADMIN )
                .password ( PASSWORD )
                .status ( UserStatus.ACTIVE )
                .build ( );

        UUID id = userAccountRepository.save ( userAccount ).getId ( );

        UserAccountEditRequest editRequest = UserAccountEditRequest.builder ( )
                .id ( id )
                .email ( newEmail )
                .firstName ( FIRSTNAME )
                .lastName ( LASTNAME )
                .build ( );

        userAccountService.editUserAccount ( id, editRequest );

        Physician physician = physicianRepository.findById ( physicianId ).get ( );

        assertEquals ( newEmail, physician.getEmail ( ) );
        assertEquals ( newEmail, userAccountRepository.findById ( id ).get ( ).getEmail ( ) );
    }
}
