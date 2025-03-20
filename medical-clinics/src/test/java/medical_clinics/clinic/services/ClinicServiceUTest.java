package medical_clinics.clinic.services;

import medical_clinics.clinic.exceptions.ExistingClinicException;
import medical_clinics.clinic.exceptions.NoSuchClinicException;
import medical_clinics.clinic.models.Clinic;
import medical_clinics.clinic.models.DaysOfWeek;
import medical_clinics.clinic.repositories.ClinicRepository;
import medical_clinics.web.dto.CreateEditClinicRequest;
import medical_clinics.web.dto.WorkDayDto;
import medical_clinics.web.dto.response.ClinicDetails;
import medical_clinics.web.dto.response.ClinicShortInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClinicServiceUTest {

    @Mock
    private ClinicRepository clinicRepository;

    @Mock
    private WorkDayService workDayService;

    @InjectMocks
    private ClinicService clinicService;

    @Test
    void when_getAllClinics_callOnEmptyRepository_thenReturnEmptyCollection () {
        when ( clinicRepository.findAll ( ) ).thenReturn ( new ArrayList<> ( ) );

        List<ClinicShortInfo> clinics = clinicService.getAllClinics ( );

        assertEquals ( 0, clinics.size ( ) );
        verify ( clinicRepository, times ( 1 ) ).findAll ( );
    }

    @Test
    void when_getAllClinics_callOnNotEmptyRepository_thenCollection () {
        List<UUID> ids = List.of ( UUID.randomUUID ( ), UUID.randomUUID ( ), UUID.randomUUID ( ), UUID.randomUUID ( ) );
        List<Clinic> clinics = List.of (
                Clinic.builder ( ).id ( ids.get ( 0 ) ).build ( ),
                Clinic.builder ( ).id ( ids.get ( 1 ) ).build ( ),
                Clinic.builder ( ).id ( ids.get ( 2 ) ).build ( ),
                Clinic.builder ( ).id ( ids.get ( 3 ) ).build ( )
        );

        when ( clinicRepository.findAll ( ) ).thenReturn ( clinics );

        List<ClinicShortInfo> clinicShortInfos = clinicService.getAllClinics ( );

        assertEquals ( clinicShortInfos.size ( ), clinics.size ( ) );
        assertTrue ( ids.containsAll ( clinicShortInfos.stream ( ).map ( ClinicShortInfo::getId ).toList ( ) ) );

        verify ( clinicRepository, times ( 1 ) ).findAll ( );
    }

    @Test
    void when_getClinicById_WithIdInRepository_ReturnsClinic () {
        UUID id = UUID.randomUUID ( );
        Clinic clinic = new Clinic ( );
        clinic.setId ( id );
        clinic.setWorkingDays ( new ArrayList<> ( ) );
        clinic.setSpecialties ( new ArrayList<> ( ) );

        when ( clinicRepository.findById ( id ) ).thenReturn ( Optional.of ( clinic ) );

        ClinicDetails clinicDetails = clinicService.getClinicById ( id );

        assertEquals ( id, clinicDetails.getId ( ) );
        verify ( clinicRepository, times ( 1 ) ).findById ( id );
    }

    @Test
    void when_getClinicById_WithIdNotInRepository_ThrowsException () {
        UUID id = UUID.randomUUID ( );

        when ( clinicRepository.findById ( id ) ).thenReturn ( Optional.empty ( ) );

        assertThrows ( NoSuchClinicException.class, () -> clinicService.getClinicById ( id ), "Clinic with provided id does not exist" );
    }

    @Test
    void when_addClinic_WithClinicInSameCityAndAddress_ThrowsException () {
        CreateEditClinicRequest clinicRequest = CreateEditClinicRequest.builder ( )
                .city ( "City" )
                .address ( "Address" )
                .build ( );

        when ( clinicRepository.findByCityAndAddress ( any ( ), any ( ) ) )
                .thenReturn ( Optional.of ( new Clinic ( ) ) );

        assertThrows (
                ExistingClinicException.class,
                () -> clinicService.addClinic ( clinicRequest )
                , "Clinic in same city and address already exists"
        );

        verify ( clinicRepository, never ( ) ).save ( any ( ) );
    }

    @Test
    void when_addClinic_WithClinicNotInConflict_ThenSaveClinic () {
        UUID id = UUID.randomUUID ( );
        Clinic clinic = new Clinic ( );
        clinic.setId ( id );
        clinic.setWorkingDays ( new ArrayList<> ( ) );

        CreateEditClinicRequest clinicRequest = CreateEditClinicRequest.builder ( )
                .city ( "City" )
                .address ( "Address" )
                .workingDays ( new ArrayList<> ( ) )
                .build ( );

        when ( clinicRepository.findByCityAndAddress ( any ( ), any ( ) ) ).thenReturn ( Optional.empty ( ) );
        when ( clinicRepository.save ( any ( Clinic.class ) ) ).thenReturn ( clinic );
        when ( workDayService.addWorkdays ( any ( ), any ( ) ) ).thenReturn ( new ArrayList<> ( ) );

        UUID newClinicId = clinicService.addClinic ( clinicRequest );

        verify ( clinicRepository, times ( 1 ) )
                .findByCityAndAddress ( "City", "Address" );

        verify ( clinicRepository, times ( 1 ) ).save ( any ( Clinic.class ) );
        verify ( workDayService, times ( 1 ) ).addWorkdays ( new ArrayList<> ( ), clinic );

        assertEquals ( id, newClinicId );
    }

    @Test
    void when_updateClinic_WithClinicNoMatchingId_ThrowsException () {
        UUID id = UUID.randomUUID ( );
        when ( clinicRepository.findById ( id ) ).thenReturn ( Optional.empty ( ) );

        assertThrows (
                NoSuchClinicException.class,
                () -> clinicService.updateClinic ( id, new CreateEditClinicRequest ( ) )
                , "Clinic with provided id does not exist"
        );

        verify ( clinicRepository, times ( 1 ) ).findById ( id );
        verify ( clinicRepository, never ( ) ).save ( any ( ) );
    }

    @Test
    void when_updateClinic_WithClinicMatchingIdButWithConflictWithAnotherClinicCityAndAddress_ThrowsException () {
        UUID id = UUID.randomUUID ( );
        UUID otherId = UUID.randomUUID ( );

        Clinic clinic = Clinic.builder ( ).id ( otherId ).city ( "City" ).address ( "Address" ).build ( );

        CreateEditClinicRequest clinicRequest = CreateEditClinicRequest.builder ( )
                .city ( "City" )
                .address ( "Address" )
                .build ( );

        when ( clinicRepository.findById ( id ) ).thenReturn (
                Optional.of ( Clinic.builder ( ).id ( id ).city ( "NoMatch" ).address ( "Different" ).build ( ) )
        );
        when ( clinicRepository.findByCityAndAddress ( "City", "Address" ) )
                .thenReturn ( Optional.of ( clinic ) );

        assertThrows (
                ExistingClinicException.class,
                () -> clinicService.updateClinic ( id, clinicRequest )
        );
        verify ( clinicRepository, never ( ) ).save ( any ( ) );
        verify ( clinicRepository, times ( 1 ) ).findById ( id );
        verify ( clinicRepository, times ( 1 ) )
                .findByCityAndAddress ( "City", "Address" );
    }

    @Test
    void when_updateClinic_WithClinicMatchingIdWithSameCityAndAddress_saveClinic () {
        UUID id = UUID.randomUUID ( );

        List<WorkDayDto> workDayDtos = List.of (
                WorkDayDto.builder ( )
                        .dayName ( DaysOfWeek.MONDAY.name ( ) )
                        .startOfWorkingDay ( LocalTime.of ( 10, 0, 0 ) )
                        .endOfWorkingDay ( LocalTime.of ( 17, 0, 0 ) )
                        .build ( )
        );

        Clinic clinic = Clinic.builder ( )
                .id ( id )
                .city ( "City" )
                .address ( "Address" )
                .workingDays ( new ArrayList<> ( ) )
                .specialties ( new ArrayList<> ( ) )
                .build ( );

        CreateEditClinicRequest clinicRequest = CreateEditClinicRequest.builder ( )
                .city ( "City" )
                .address ( "Address" )
                .description ( "Test" )
                .workingDays ( workDayDtos )
                .build ( );

        when ( clinicRepository.findById ( id ) ).thenReturn ( Optional.of ( clinic ) );
        when ( clinicRepository.save ( any ( ) ) ).thenReturn ( clinic );
        when ( workDayService.updateWorkDays ( clinic, workDayDtos ) ).thenReturn ( new ArrayList<> ( ) );

        clinicService.updateClinic ( id, clinicRequest );

        verify ( clinicRepository, times ( 1 ) ).findById ( id );
        verify ( clinicRepository, never ( ) ).findByCityAndAddress ( any ( ), any ( ) );
        verify ( clinicRepository, times ( 1 ) ).save ( any ( ) );
        verify ( workDayService, times ( 1 ) ).updateWorkDays ( clinic, workDayDtos );
    }

    @Test
    void when_updateClinic_WithClinicMatchingIdWithEditedLocationButNoConflict_saveClinic () {
        UUID id = UUID.randomUUID ( );

        List<WorkDayDto> workDayDtos = List.of (
                WorkDayDto.builder ( )
                        .dayName ( DaysOfWeek.MONDAY.name ( ) )
                        .startOfWorkingDay ( LocalTime.of ( 10, 0, 0 ) )
                        .endOfWorkingDay ( LocalTime.of ( 17, 0, 0 ) )
                        .build ( )
        );

        Clinic clinic = Clinic.builder ( )
                .id ( id )
                .city ( "City" )
                .address ( "Address" )
                .workingDays ( new ArrayList<> ( ) )
                .specialties ( new ArrayList<> ( ) )
                .build ( );

        CreateEditClinicRequest clinicRequest = CreateEditClinicRequest.builder ( )
                .city ( "City2" )
                .address ( "Address2" )
                .description ( "Test2" )
                .workingDays ( workDayDtos )
                .build ( );

        when ( clinicRepository.findById ( id ) ).thenReturn ( Optional.of ( clinic ) );
        when ( clinicRepository.findByCityAndAddress ( "City2", "Address2" ) )
                .thenReturn ( Optional.empty ( ) );

        when ( clinicRepository.save ( any ( ) ) ).thenReturn ( new Clinic ( ) );
        when ( workDayService.updateWorkDays ( clinic, workDayDtos ) ).thenReturn ( new ArrayList<> ( ) );

        clinicService.updateClinic ( id, clinicRequest );

        verify ( clinicRepository, times ( 1 ) ).findById ( id );
        verify ( clinicRepository, times ( 1 ) )
                .findByCityAndAddress ( "City2", "Address2" );
        verify ( clinicRepository, times ( 1 ) ).save ( any ( ) );
        verify ( workDayService, times ( 1 ) ).updateWorkDays ( clinic, workDayDtos );
    }

    @Test
    void when_updateClinic_WithClinicMatchingIdWithEditedAddressButNoConflict_saveClinic () {
        UUID id = UUID.randomUUID ( );

        List<WorkDayDto> workDayDtos = List.of (
                WorkDayDto.builder ( )
                        .dayName ( DaysOfWeek.MONDAY.name ( ) )
                        .startOfWorkingDay ( LocalTime.of ( 10, 0, 0 ) )
                        .endOfWorkingDay ( LocalTime.of ( 17, 0, 0 ) )
                        .build ( )
        );

        Clinic clinic = Clinic.builder ( )
                .id ( id )
                .city ( "City" )
                .address ( "Address" )
                .workingDays ( new ArrayList<> ( ) )
                .specialties ( new ArrayList<> ( ) )
                .build ( );

        CreateEditClinicRequest clinicRequest = CreateEditClinicRequest.builder ( )
                .city ( "City" )
                .address ( "Address2" )
                .description ( "Test2" )
                .workingDays ( workDayDtos )
                .build ( );

        when ( clinicRepository.findById ( id ) ).thenReturn ( Optional.of ( clinic ) );
        when ( clinicRepository.findByCityAndAddress ( "City", "Address2" ) )
                .thenReturn ( Optional.empty ( ) );

        when ( clinicRepository.save ( any ( ) ) ).thenReturn ( new Clinic ( ) );
        when ( workDayService.updateWorkDays ( clinic, workDayDtos ) ).thenReturn ( new ArrayList<> ( ) );

        clinicService.updateClinic ( id, clinicRequest );

        verify ( clinicRepository, times ( 1 ) ).findById ( id );
        verify ( clinicRepository, times ( 1 ) )
                .findByCityAndAddress ( "City", "Address2" );
        verify ( clinicRepository, times ( 1 ) ).save ( any ( ) );
        verify ( workDayService, times ( 1 ) ).updateWorkDays ( clinic, workDayDtos );
    }

    @Test
    void when_deleteClinic_WithIdNotExist_ThrowsException () {
        UUID id = UUID.randomUUID ( );

        when ( clinicRepository.findById ( id ) ).thenReturn ( Optional.empty ( ) );

        assertThrows ( NoSuchClinicException.class, () -> clinicService.deleteClinic ( id ) );

        verify ( clinicRepository, times ( 1 ) ).findById ( id );
        verify ( clinicRepository, never ( ) ).deleteById ( id );
    }

    @Test
    void when_deleteClinic_WithExistingId_deleteClinic () {
        UUID id = UUID.randomUUID ( );

        Clinic clinic = Clinic.builder ( ).id ( id ).build ( );

        when ( clinicRepository.findById ( id ) ).thenReturn ( Optional.of ( clinic ) );

        clinicService.deleteClinic ( id );

        verify ( clinicRepository, times ( 1 ) ).findById ( id );
        verify ( clinicRepository, times ( 1 ) ).deleteById ( id );
    }

    @Test
    void when_getClinicIdByCityAndAddress_WithNoExistingLocation_ThrowsException () {
        String city = "City";
        String address = "Address";

        when ( clinicRepository.findByCityAndAddress ( city, address ) ).thenReturn ( Optional.empty ( ) );

        assertThrows (
                NoSuchClinicException.class,
                () -> clinicService.getClinicIdByCityAndAddress ( city, address )
        );

        verify ( clinicRepository, times ( 1 ) ).findByCityAndAddress ( city, address );
    }

    @Test
    void when_getClinicIdByCityAndAddress_WithExistingLocation_ReturnClinic () {
        UUID id = UUID.randomUUID ( );
        String city = "City";
        String address = "Address";

        Clinic clinic = Clinic.builder ( )
                .id ( id )
                .city ( "City" )
                .address ( "Address" )
                .workingDays ( new ArrayList<> ( ) )
                .specialties ( new ArrayList<> ( ) )
                .build ( );

        when ( clinicRepository.findByCityAndAddress ( city, address ) ).thenReturn ( Optional.of ( clinic ) );

        Clinic foundClinic = clinicService.getClinicIdByCityAndAddress ( city, address );

        assertEquals ( id, foundClinic.getId ( ) );
        assertEquals ( city, foundClinic.getCity ( ) );
        assertEquals ( address, foundClinic.getAddress ( ) );

        verify ( clinicRepository, times ( 1 ) ).findByCityAndAddress ( city, address );
    }
}