package medical_clinics.clinic.services;

import jakarta.transaction.Transactional;
import medical_clinics.clinic.models.Clinic;
import medical_clinics.clinic.repositories.ClinicRepository;
import medical_clinics.physician.model.Physician;
import medical_clinics.specialty.model.Specialty;
import medical_clinics.specialty.model.SpecialtyName;
import medical_clinics.specialty.service.SpecialtyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class ClinicServiceITests {
    private static final String STRING = "test";
    private static final String PICTURE_URL = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bd/Test.svg/1200px-Test.svg.png";

    @Autowired
    private ClinicService clinicService;

    @Autowired
    private ClinicRepository clinicRepository;

    @Autowired
    private SpecialtyService specialtyService;

    @Test
    @Transactional
    void when_addPhysicianSpeciality_SpecialityNotInTheList_thenExpectClinicAddSpeciality () {
        Clinic clinic = Clinic.builder ( )
                .city ( STRING )
                .address ( STRING )
                .workingDays ( new ArrayList<> ( ) )
                .specialties ( new ArrayList<> ( ) )
                .pictureUrl ( PICTURE_URL )
                .description ( STRING )
                .phoneNumber ( "+1234567890" )
                .identificationNumber ( "AA123456789" )
                .build ( );

        Clinic savedClinic = clinicRepository.save ( clinic );

        Specialty specialty = specialtyService.getSpecialtyByName ( SpecialtyName.ALLERGIST.name ( ) );

        Physician physician = Physician.builder ( )
                .firstName ( STRING )
                .lastName ( STRING )
                .identificationNumber ( "AA111111111" )
                .email ( "test@test.com" )
                .specialty ( specialty )
                .workplace ( savedClinic )
                .build ( );

        clinicService.addPhysicianSpeciality ( physician );

        List<Specialty> specialties = (List<Specialty>) clinicRepository.findById ( savedClinic.getId ( ) ).get ( ).getSpecialties ( );

        assertEquals ( 1, specialties.size ( ) );
        assertTrue ( specialties.contains ( specialty ) );
    }
}
