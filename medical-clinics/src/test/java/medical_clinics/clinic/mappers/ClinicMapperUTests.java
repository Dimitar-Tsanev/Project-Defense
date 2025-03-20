package medical_clinics.clinic.mappers;

import medical_clinics.clinic.models.Clinic;
import medical_clinics.clinic.models.DaysOfWeek;
import medical_clinics.clinic.models.WorkDay;
import medical_clinics.specialty.model.Specialty;
import medical_clinics.specialty.model.SpecialtyName;
import medical_clinics.web.dto.CreateEditClinicRequest;
import medical_clinics.web.dto.response.ClinicDetails;
import medical_clinics.web.dto.response.ClinicShortInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ClinicMapperUTests {
    private static final LocalTime START_TIME = LocalTime.of ( 10, 0, 0 );
    private static final LocalTime END_TIME = LocalTime.of ( 17, 0, 0 );
    private static final String CLINIC_CITY = "City";
    private static final String CLINIC_ADDRESS = "Address";
    private static final String CLINIC_NUMBER = "123456789";
    private static final String URL = "https://www.clinic-clinics.com/";

    private static final UUID _UUID = UUID.randomUUID ( );


    private static final WorkDay WORK_DAY = WorkDay.builder ( )
            .dayOfWeek ( DaysOfWeek.MONDAY )
            .startOfWorkingDay ( START_TIME )
            .endOfWorkingDay ( END_TIME )
            .build ( );

    private static final Specialty SPECIALTY = new Specialty ( SpecialtyName.ALLERGIST );

    @Test
    void when_mapClinicToClinicShortInfo_expectClinicShortInfo () {
        Clinic clinic = Clinic.builder ( )
                .id ( _UUID )
                .city ( CLINIC_CITY )
                .address ( CLINIC_ADDRESS )
                .pictureUrl ( URL )
                .build ( );

        ClinicShortInfo clinicShortInfo = ClinicMapper.mapClinicToClinicShortInfo ( clinic );

        assertEquals ( _UUID, clinicShortInfo.getId ( ) );
        assertEquals ( CLINIC_CITY, clinicShortInfo.getCity ( ) );
        assertEquals ( CLINIC_ADDRESS, clinicShortInfo.getAddress ( ) );
        assertEquals ( URL, clinicShortInfo.getPictureUrl ( ) );
    }

    @Test
    void when_mapClinicToClinicDetails_expectClinicDetailed () {
        String dayName = "Monday";
        String description = CLINIC_CITY + " " + CLINIC_ADDRESS;

        Clinic clinic = Clinic.builder ( )
                .id ( _UUID )
                .city ( CLINIC_CITY )
                .address ( CLINIC_ADDRESS )
                .workingDays ( List.of ( WORK_DAY ) )
                .description ( description )
                .phoneNumber ( "+" + CLINIC_NUMBER )
                .identificationNumber ( CLINIC_NUMBER )
                .pictureUrl ( URL )
                .specialties ( List.of ( SPECIALTY ) )
                .build ( );

        ClinicDetails clinicDetails = ClinicMapper.mapClinicToClinicDetails ( clinic );

        assertEquals ( _UUID, clinicDetails.getId ( ) );
        assertEquals ( CLINIC_CITY, clinicDetails.getCity ( ) );
        assertEquals ( CLINIC_ADDRESS, clinicDetails.getAddress ( ) );
        assertEquals ( URL, clinicDetails.getPictureUrl ( ) );
        assertEquals ( description, clinicDetails.getDescription ( ) );
        assertEquals ( "+" + CLINIC_NUMBER, clinicDetails.getPhoneNumber ( ) );

        assertEquals ( 1, clinicDetails.getSpecialties ( ).size ( ) );
        assertEquals ( 1, clinicDetails.getWorkingDays ( ).size ( ) );

        String day = clinicDetails.getWorkingDays ( ).iterator ( ).next ( ).getDayName ( );
        String speciality = clinicDetails.getSpecialties ( ).iterator ( ).next ( ).getName ( );

        assertEquals ( dayName, day );
        assertEquals ( SPECIALTY.getName ( ).name ( ), speciality );
    }

    @Test
    void when_mapClinicToClinicDetails_WithoutWorkdays_expectClinicDetailed () {
        String description = CLINIC_CITY + " " + CLINIC_ADDRESS;

        Clinic clinic = Clinic.builder ( )
                .id ( _UUID )
                .city ( CLINIC_CITY )
                .address ( CLINIC_ADDRESS )
                .workingDays ( new ArrayList<> ( ) )
                .description ( description )
                .phoneNumber ( "+" + CLINIC_NUMBER )
                .identificationNumber ( CLINIC_NUMBER )
                .pictureUrl ( URL )
                .specialties ( List.of ( SPECIALTY ) )
                .build ( );

        ClinicDetails clinicDetails = ClinicMapper.mapClinicToClinicDetails ( clinic );

        assertEquals ( _UUID, clinicDetails.getId ( ) );
        assertEquals ( CLINIC_CITY, clinicDetails.getCity ( ) );
        assertEquals ( CLINIC_ADDRESS, clinicDetails.getAddress ( ) );
        assertEquals ( URL, clinicDetails.getPictureUrl ( ) );
        assertEquals ( description, clinicDetails.getDescription ( ) );
        assertEquals ( "+" + CLINIC_NUMBER, clinicDetails.getPhoneNumber ( ) );

        assertEquals ( 1, clinicDetails.getSpecialties ( ).size ( ) );
        assertEquals ( 0, clinicDetails.getWorkingDays ( ).size ( ) );

        String speciality = clinicDetails.getSpecialties ( ).iterator ( ).next ( ).getName ( );

        assertEquals ( SPECIALTY.getName ( ).name ( ), speciality );
    }

    @Test
    void when_mapClinicToClinicDetails_WithoutSpecialities_expectClinicDetailed () {
        String dayName = "Monday";
        String description = CLINIC_CITY + " " + CLINIC_ADDRESS;

        Clinic clinic = Clinic.builder ( )
                .id ( _UUID )
                .city ( CLINIC_CITY )
                .address ( CLINIC_ADDRESS )
                .workingDays ( List.of ( WORK_DAY ) )
                .description ( description )
                .phoneNumber ( "+" + CLINIC_NUMBER )
                .identificationNumber ( CLINIC_NUMBER )
                .pictureUrl ( URL )
                .specialties ( new ArrayList<> ( ) )
                .build ( );

        ClinicDetails clinicDetails = ClinicMapper.mapClinicToClinicDetails ( clinic );

        assertEquals ( _UUID, clinicDetails.getId ( ) );
        assertEquals ( CLINIC_CITY, clinicDetails.getCity ( ) );
        assertEquals ( CLINIC_ADDRESS, clinicDetails.getAddress ( ) );
        assertEquals ( URL, clinicDetails.getPictureUrl ( ) );
        assertEquals ( description, clinicDetails.getDescription ( ) );
        assertEquals ( "+" + CLINIC_NUMBER, clinicDetails.getPhoneNumber ( ) );

        assertEquals ( 0, clinicDetails.getSpecialties ( ).size ( ) );
        assertEquals ( 1, clinicDetails.getWorkingDays ( ).size ( ) );

        String day = clinicDetails.getWorkingDays ( ).iterator ( ).next ( ).getDayName ( );

        assertEquals ( dayName, day );
    }

    @Test
    void when_mapToClinic_expectClinic () {
        String description = CLINIC_CITY + " " + CLINIC_ADDRESS;

        CreateEditClinicRequest clinicRequest = CreateEditClinicRequest.builder ( )
                .city ( CLINIC_CITY )
                .address ( CLINIC_ADDRESS )
                .description ( description )
                .phoneNumber ( "+" + CLINIC_NUMBER )
                .identificationNumber ( CLINIC_NUMBER )
                .pictureUrl ( URL )
                .build ( );

        Clinic clinic = ClinicMapper.mapToClinic ( clinicRequest );

        assertNull ( clinic.getId ( ) );
        assertEquals ( CLINIC_CITY, clinic.getCity ( ) );
        assertEquals ( CLINIC_ADDRESS, clinic.getAddress ( ) );
        assertEquals ( description, clinic.getDescription ( ) );
        assertEquals ( "+" + CLINIC_NUMBER, clinic.getPhoneNumber ( ) );
        assertEquals ( CLINIC_NUMBER, clinic.getIdentificationNumber ( ) );
        assertEquals ( URL, clinic.getPictureUrl ( ) );
    }

    @Test
    void when_mapClinicToClinicDetails_ClinicIsBlankObject_expectThrow () {
        Clinic clinic = Clinic.builder ( ).build ( );

        assertThrows ( NullPointerException.class, () -> ClinicMapper.mapClinicToClinicDetails ( clinic ) );
    }

}
