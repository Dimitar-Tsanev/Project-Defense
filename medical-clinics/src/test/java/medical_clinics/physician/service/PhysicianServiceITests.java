package medical_clinics.physician.service;

import jakarta.transaction.Transactional;
import medical_clinics.clinic.models.Clinic;
import medical_clinics.clinic.repositories.ClinicRepository;
import medical_clinics.patient.model.Patient;
import medical_clinics.patient.repository.PatientRepository;
import medical_clinics.physician.model.Physician;
import medical_clinics.physician.repository.PhysicianRepository;
import medical_clinics.shared.exception.PersonalInformationDontMatchException;
import medical_clinics.specialty.model.Specialty;
import medical_clinics.specialty.model.SpecialtyName;
import medical_clinics.user_account.model.Role;
import medical_clinics.user_account.model.UserAccount;
import medical_clinics.user_account.model.UserStatus;
import medical_clinics.user_account.repository.UserAccountRepository;
import medical_clinics.web.dto.CreatePhysician;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class PhysicianServiceITests {
    private static final String CITY = "Somewhere";
    private static final String ADDRESS = "Somewhere 1";
    private static final String EMAIL = "some@mail.com";

    @Autowired
    PhysicianService physicianService;

    @Autowired
    PhysicianRepository physicianRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    UserAccountRepository userAccountRepository;

    @Autowired
    ClinicRepository clinicRepository;

    @Test
    void when_addPhysician_withPatientEmailConflict_thenThrowsException () {
        buildPatient ( );

        CreatePhysician physician = CreatePhysician.builder ( )
                .firstName ( "John" )
                .lastName ( "Smith" )
                .identificationNumber ( "123456AAA" )
                .abbreviation ( "m.d" )
                .pictureUrl ( "https://example.com/image.jpg" )
                .description ( "Some description" )
                .email ( EMAIL )
                .workplaceCity ( CITY )
                .workplaceAddress ( ADDRESS )
                .specialty ( "CardiologY" )
                .build ( );

        assertThrows ( PersonalInformationDontMatchException.class, () -> physicianService.addPhysician ( physician ) );
    }

    @Test
    @Transactional
    void when_addPhysician_withCorrectData_thenShouldAddPhysicianAndReturnId () {
        Clinic clinic = buildClinic ( );

        UUID accountId = buildUserAccount ();

        String firstName = "John";
        String lastName = "Smith";
        String identificationNumber = "123456AAA";
        String abbreviation = "m.d";
        String pictureUrl = "https://example.com/image.jpg";
        String description = "Some description";

        CreatePhysician physicianRequest = CreatePhysician.builder ( )
                .firstName ( firstName )
                .lastName ( lastName )
                .identificationNumber ( identificationNumber )
                .abbreviation ( abbreviation )
                .pictureUrl ( pictureUrl )
                .description ( description )
                .email ( EMAIL )
                .workplaceCity ( CITY )
                .workplaceAddress ( ADDRESS )
                .specialty ( "CardiologY" )
                .build ( );

        UUID physicianId = physicianService.addPhysician ( physicianRequest );

        Physician physician = physicianRepository.findById ( physicianId ).get ( );

        List<Specialty> specialties = (List<Specialty>) clinicRepository.findById ( clinic.getId ( ) )
                .get ( ).getSpecialties ( );

        UserAccount account = userAccountRepository.findById ( accountId ).get ( );

        assertEquals ( firstName, physician.getFirstName ( ) );
        assertEquals ( lastName, physician.getLastName ( ) );
        assertEquals ( identificationNumber, physician.getIdentificationNumber ( ) );
        assertEquals ( abbreviation, physician.getAbbreviation ( ) );
        assertEquals ( pictureUrl, physician.getPictureUrl ( ) );
        assertEquals ( description, physician.getDescription ( ) );
        assertEquals ( EMAIL, physician.getEmail ( ) );
        assertEquals ( clinic.getId ( ), physician.getWorkplace ( ).getId ( ) );
        assertEquals ( SpecialtyName.CARDIOLOGY, physician.getSpecialty ( ).getName ( ) );

        assertEquals ( 1, specialties.size ( ) );
        assertEquals ( SpecialtyName.CARDIOLOGY, specialties.getFirst ( ).getName ( ) );
        assertEquals ( Role.PHYSICIAN, account.getRole () );
    }

    private UUID buildUserAccount () {
        return userAccountRepository.save ( UserAccount.builder ( )
                .email ( EMAIL )
                .role ( Role.PATIENT )
                .status ( UserStatus.ACTIVE )
                .password ( "someNotHashedPassword" )
                .build ( ) ).getId ( );
    }

    private void buildPatient () {
        patientRepository.save ( Patient.builder ( )
                .email ( EMAIL )
                .firstName ( "Test" )
                .lastName ( "Test" )
                .build ( ) );
    }

    private Clinic buildClinic () {
        return clinicRepository.save (
                Clinic.builder ( )
                        .city ( CITY )
                        .address ( ADDRESS )
                        .description ( "some" )
                        .pictureUrl ( "https://somewhere.com" )
                        .phoneNumber ( "123456789" )
                        .identificationNumber ( "123456789" )
                        .build ( )
        );
    }
}
