package medical_clinics.physician.service;

import medical_clinics.clinic.models.Clinic;
import medical_clinics.physician.exceptions.PhysicianAlreadyExistException;
import medical_clinics.physician.exceptions.PhysicianNotFoundException;
import medical_clinics.physician.model.Physician;
import medical_clinics.physician.repository.PhysicianRepository;
import medical_clinics.schedule.services.DailyScheduleService;
import medical_clinics.specialty.model.Specialty;
import medical_clinics.specialty.model.SpecialtyName;
import medical_clinics.web.dto.CreatePhysician;
import medical_clinics.web.dto.NewDaySchedule;
import medical_clinics.web.dto.response.PhysicianInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PhysicianServiceUTests {

    @Mock
    PhysicianRepository physicianRepository;

    @Mock
    DailyScheduleService dailyScheduleService;

    @InjectMocks
    PhysicianService physicianService;

    @Test
    void when_addPhysician_withPhysicianEmailConflict_thenThrowsException () {
        String physicianEmail = "physician@email.com";
        String identificationCode = "123456AAA";

        CreatePhysician physician = CreatePhysician.builder ( )
                .email ( physicianEmail )
                .identificationNumber ( identificationCode )
                .build ( );

        when ( physicianRepository.findByEmail ( physicianEmail ) ).thenReturn ( Optional.of ( new Physician ( ) ) );
        when ( physicianRepository.findByIdentificationNumber ( identificationCode ) )
                .thenReturn ( Optional.empty ( ) );

        assertThrows ( PhysicianAlreadyExistException.class, () -> physicianService.addPhysician ( physician ) );

        verify ( physicianRepository, times ( 1 ) ).findByEmail ( physicianEmail );
        verify ( physicianRepository, times ( 1 ) )
                .findByIdentificationNumber ( identificationCode );
    }

    @Test
    void when_addPhysician_withPhysicianIdentificationNumberConflict_thenThrowsException () {
        String physicianEmail = "physician@email.com";
        String identificationCode = "123456AAA";

        CreatePhysician physician = CreatePhysician.builder ( )
                .email ( physicianEmail )
                .identificationNumber ( identificationCode )
                .build ( );

        when ( physicianRepository.findByEmail ( physicianEmail ) ).thenReturn ( Optional.empty ( ) );
        when ( physicianRepository.findByIdentificationNumber ( identificationCode ) )
                .thenReturn ( Optional.of ( new Physician ( ) ) );

        assertThrows ( PhysicianAlreadyExistException.class, () -> physicianService.addPhysician ( physician ) );

        verify ( physicianRepository, times ( 1 ) ).findByEmail ( physicianEmail );
        verify ( physicianRepository, times ( 1 ) )
                .findByIdentificationNumber ( identificationCode );
    }

    @Test
    void when_addPhysician_withPhysicianBothUniqueDataConflict_thenThrowsException () {
        String physicianEmail = "physician@email.com";
        String identificationCode = "123456AAA";

        CreatePhysician physician = CreatePhysician.builder ( )
                .email ( physicianEmail )
                .identificationNumber ( identificationCode )
                .build ( );

        when ( physicianRepository.findByEmail ( physicianEmail ) ).thenReturn ( Optional.of ( new Physician ( ) ) );
        when ( physicianRepository.findByIdentificationNumber ( identificationCode ) )
                .thenReturn ( Optional.of ( new Physician ( ) ) );

        assertThrows ( PhysicianAlreadyExistException.class, () -> physicianService.addPhysician ( physician ) );

        verify ( physicianRepository, times ( 1 ) ).findByEmail ( physicianEmail );
        verify ( physicianRepository, times ( 1 ) )
                .findByIdentificationNumber ( identificationCode );
    }

    @Test
    void when_generateSchedule_withPhysicianNotFound_thenThrowsException () {
        UUID accountId = UUID.randomUUID ( );

        when ( physicianRepository.findByUserAccount_Id ( accountId ) ).thenReturn ( Optional.empty ( ) );

        assertThrows ( PhysicianNotFoundException.class,
                () -> physicianService.generateSchedule ( accountId, new ArrayList<> ( ) ) );

        verify ( physicianRepository, times ( 1 ) ).findByUserAccount_Id ( accountId );
    }

    @Test
    void when_generateSchedule_withPhysician_thenForEachScheduledServiceIsCalled () {
        UUID accountId = UUID.randomUUID ( );

        List<NewDaySchedule> daySchedules = List.of (
                new NewDaySchedule ( ), new NewDaySchedule ( ), new NewDaySchedule ( ), new NewDaySchedule ( )
        );

        when ( physicianRepository.findByUserAccount_Id ( accountId ) ).thenReturn ( Optional.of ( new Physician ( ) ) );

        physicianService.generateSchedule ( accountId, daySchedules );

        verify ( physicianRepository, times ( 1 ) ).findByUserAccount_Id ( accountId );
        verify ( dailyScheduleService, times ( daySchedules.size ( ) ) ).generateDaySchedule ( any ( ), any ( ) );
    }

    @Test
    void when_getPhysicianInfo_withPhysicianNotFound_thenThrowsException () {
        UUID physicianId = UUID.randomUUID ( );

        when ( physicianRepository.findById ( physicianId ) ).thenReturn ( Optional.empty ( ) );

        assertThrows ( PhysicianNotFoundException.class,
                () -> physicianService.getPhysicianInfo ( physicianId ) );

        verify ( physicianRepository, times ( 1 ) ).findById ( physicianId );
    }

    @Test
    void when_getPhysicianInfo_withPhysicianFound_thenReturnPhysicianInfo () {
        UUID physicianId = UUID.randomUUID ( );

        Physician physician = Physician.builder ( )
                .id ( physicianId )
                .firstName ( "John" )
                .lastName ( "Doe" )
                .abbreviation ( "m.d." )
                .description ( "Some text" )
                .pictureUrl ( "https://somewhere.com" )
                .workplace ( buildClinic ( ) )
                .specialty ( new Specialty ( SpecialtyName.ALLERGIST ) )
                .build ( );

        when ( physicianRepository.findById ( physicianId ) ).thenReturn ( Optional.of ( physician ) );

        PhysicianInfo info = physicianService.getPhysicianInfo ( physicianId );

        verify ( physicianRepository, times ( 1 ) ).findById ( physicianId );

        assertEquals ( physicianId, info.getPhysicianId ( ) );
        assertEquals ( "John", info.getFirstName ( ) );
        assertEquals ( "Doe", info.getLastName ( ) );
        assertEquals ( "m.d.", info.getAbbreviation ( ) );
        assertEquals ( "https://somewhere.com", info.getPictureUrl ( ) );
        assertEquals ( "Some text", info.getDescription ( ) );
        assertEquals ( "City, Address", info.getWorkplace ( ) );
        assertEquals ( "Allergist", info.getSpecialty ( ) );
    }

    @Test
    void when_getPhysiciansByClinicAndSpeciality_withFoundList_returnsList () {
        when ( physicianRepository.findAllByWorkplace_IdAndSpecialty_Id ( any ( ), any ( ) ) )
                .thenReturn ( List.of ( Physician.builder ( )
                        .id ( UUID.randomUUID ( ) )
                        .firstName ( "John" )
                        .lastName ( "Doe" )
                        .abbreviation ( "m.d." )
                        .description ( "Some text" )
                        .pictureUrl ( "https://somewhere.com" )
                        .workplace ( buildClinic ( ) )
                        .specialty ( new Specialty ( SpecialtyName.ALLERGIST ) )
                        .build ( ) )
                );

        List<PhysicianInfo> physicianInfos = physicianService.getPhysiciansByClinicAndSpeciality (
                UUID.randomUUID ( ), UUID.randomUUID ( )
        );

        verify ( physicianRepository, times ( 1 ) )
                .findAllByWorkplace_IdAndSpecialty_Id ( any ( ), any ( ) );

        assertEquals ( 1, physicianInfos.size ( ) );
    }

    @Test
    void when_getPhysiciansByClinicAndSpeciality_withNotFound_returnsEmptyList () {
        when ( physicianRepository.findAllByWorkplace_IdAndSpecialty_Id ( any ( ), any ( ) ) )
                .thenReturn ( new ArrayList<> ( ) );

        List<PhysicianInfo> physicianInfos = physicianService.getPhysiciansByClinicAndSpeciality (
                UUID.randomUUID ( ), UUID.randomUUID ( )
        );

        verify ( physicianRepository, times ( 1 ) )
                .findAllByWorkplace_IdAndSpecialty_Id ( any ( ), any ( ) );

        assertTrue ( physicianInfos.isEmpty () );
    }

    @Test
    void when_getPhysicianIdByUserAccountId_withPhysicianNotFound_thenThrowsException () {
        UUID accountId = UUID.randomUUID ( );

        when ( physicianRepository.findByUserAccount_Id ( accountId ) ).thenReturn ( Optional.empty ( ) );

        assertThrows ( PhysicianNotFoundException.class,
                () -> physicianService.getPhysicianIdByUserAccountId ( accountId ) );

        verify ( physicianRepository, times ( 1 ) ).findByUserAccount_Id ( accountId );
    }

    @Test
    void when_getPhysicianIdByUserAccountId_withPhysicianFound_thenReturnPhysicianId () {
        UUID accountId = UUID.randomUUID ( );
        UUID physicianId = UUID.randomUUID ( );

        when ( physicianRepository.findByUserAccount_Id ( accountId ) )
                .thenReturn ( Optional.of ( Physician.builder ( ).id ( physicianId ).build ( ) ) );

        UUID foundId = physicianService.getPhysicianIdByUserAccountId ( accountId );

        verify ( physicianRepository, times ( 1 ) ).findByUserAccount_Id ( accountId );
        assertEquals ( foundId, physicianId );
    }

    private Clinic buildClinic () {
        return Clinic.builder ( )
                .city ( "City" )
                .address ( "Address" )
                .description ( "some" )
                .pictureUrl ( "https://somewhere.com" )
                .phoneNumber ( "123456789" )
                .identificationNumber ( "123456789" )
                .build ( );
    }
}
