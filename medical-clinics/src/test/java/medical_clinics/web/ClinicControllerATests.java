package medical_clinics.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import medical_clinics.clinic.exceptions.NoSuchClinicException;
import medical_clinics.clinic.services.ClinicService;
import medical_clinics.shared.config.SecurityConfig;
import medical_clinics.web.dto.CreateEditClinicRequest;
import medical_clinics.web.dto.WorkDayDto;
import medical_clinics.web.dto.response.ClinicDetails;
import medical_clinics.web.dto.response.ClinicShortInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@WebMvcTest(ClinicController.class)
public class ClinicControllerATests {
    private static final String PICTURE_URL = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bd/Test.svg/1200px-Test.svg.png";
    private static final String STRING = "Some test string";

    @MockitoBean
    ClinicService clinicService;

    @Autowired
    MockMvc mockMvc;

    ObjectMapper mapper;

    @BeforeEach
    void setUp () {
        mapper = new ObjectMapper ( );
        mapper.findAndRegisterModules ( );
    }

    @Test
    void when_addNewClinic_withNoJWT_thenExpectUnauthorized401 () throws Exception {
        MockHttpServletRequestBuilder request = post ( "/clinics/clinic/new" );

        mockMvc.perform ( request ).andExpect ( status ( ).isUnauthorized ( ) );

        verify ( clinicService, never ( ) ).addClinic ( any ( ) );
    }

    @Test
    void when_addNewClinic_withJWTButNoAdmin_thenExpectForbidden403 () throws Exception {
        WorkDayDto workDayDto = WorkDayDto.builder ( )
                .dayName ( "MonDay" )
                .startOfWorkingDay ( LocalTime.of ( 8, 0 ) )
                .endOfWorkingDay ( LocalTime.of ( 17, 0 ) )
                .build ( );

        CreateEditClinicRequest clinicRequest = CreateEditClinicRequest.builder ( )
                .description ( STRING )
                .workingDays ( List.of ( workDayDto ) )
                .pictureUrl ( PICTURE_URL )
                .identificationNumber ( "AA11111111" )
                .phoneNumber ( "1234567890" )
                .city ( STRING )
                .address ( STRING )
                .build ( );

        MockHttpServletRequestBuilder request = post ( "/clinics/clinic/new" )
                .with ( jwt ( )
                        .authorities ( new SimpleGrantedAuthority ( "ROLE_PHYSICIAN" ) )
                )
                .contentType ( MediaType.APPLICATION_JSON )
                .content ( mapper.writeValueAsString ( clinicRequest ) );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isForbidden ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( clinicService, never ( ) ).addClinic ( any ( ) );
    }

    @Test
    void when_addNewClinic_withValidDto_thenExpectCreated201 () throws Exception {
        WorkDayDto workDayDto = WorkDayDto.builder ( )
                .dayName ( "MonDay" )
                .startOfWorkingDay ( LocalTime.of ( 8, 0 ) )
                .endOfWorkingDay ( LocalTime.of ( 17, 0 ) )
                .build ( );

        CreateEditClinicRequest clinicRequest = CreateEditClinicRequest.builder ( )
                .description ( STRING )
                .workingDays ( List.of ( workDayDto ) )
                .pictureUrl ( PICTURE_URL )
                .identificationNumber ( "AA11111111" )
                .phoneNumber ( "1234567890" )
                .city ( STRING )
                .address ( STRING )
                .build ( );

        UUID id = UUID.randomUUID ( );

        when ( clinicService.addClinic ( any ( ) ) ).thenReturn ( id );

        MockHttpServletRequestBuilder request = post ( "/clinics/clinic/new" )
                .with ( jwt ( )
                        .authorities ( new SimpleGrantedAuthority ( "ROLE_ADMIN" ) )
                )
                .contentType ( MediaType.APPLICATION_JSON )
                .content ( mapper.writeValueAsString ( clinicRequest ) );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isCreated ( ) )
                .andExpect ( header ( ).string (
                        HttpHeaders.LOCATION, "http://localhost:8080/api/v0/clinics/clinic/" + id
                ) );

        verify ( clinicService, times ( 1 ) ).addClinic ( any ( ) );
    }

    @Test
    void when_addNewClinic_withInvalidWorkdayName_thenExpectBadRequest400 () throws Exception {
        WorkDayDto workDayDto = WorkDayDto.builder ( )
                .dayName ( "SomeDay" )
                .startOfWorkingDay ( LocalTime.of ( 8, 0 ) )
                .endOfWorkingDay ( LocalTime.of ( 17, 0 ) )
                .build ( );

        CreateEditClinicRequest clinicRequest = CreateEditClinicRequest.builder ( )
                .description ( STRING )
                .workingDays ( List.of ( workDayDto ) )
                .pictureUrl ( PICTURE_URL )
                .identificationNumber ( "AA11111111" )
                .phoneNumber ( "1234567890" )
                .city ( STRING )
                .address ( STRING )
                .build ( );

        UUID id = UUID.randomUUID ( );

        when ( clinicService.addClinic ( any ( ) ) ).thenReturn ( id );

        MockHttpServletRequestBuilder request = post ( "/clinics/clinic/new" )
                .with ( jwt ( )
                        .authorities ( new SimpleGrantedAuthority ( "ROLE_ADMIN" ) )
                )
                .contentType ( MediaType.APPLICATION_JSON )
                .content ( mapper.writeValueAsString ( clinicRequest ) );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isBadRequest ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( clinicService, never ( ) ).addClinic ( any ( ) );
    }

    @Test
    void when_getAllClinics_withNoClinics_thenReturnEmptyListStatusOk200 () throws Exception {
        when ( clinicService.getAllClinics ( ) ).thenReturn ( new ArrayList<> ( ) );

        MockHttpServletRequestBuilder request = get ( "/clinics/" );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isOk ( ) )
                .andExpect ( jsonPath ( "$", hasSize ( 0 ) ) );

        verify ( clinicService, times ( 1 ) ).getAllClinics ( );
    }

    @Test
    void when_getAllClinics_withClinics_thenReturnEmptyListStatusOk200 () throws Exception {
        when ( clinicService.getAllClinics ( ) ).thenReturn ( List.of ( new ClinicShortInfo ( ) ) );

        MockHttpServletRequestBuilder request = get ( "/clinics/" );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isOk ( ) )
                .andExpect ( jsonPath ( "$", hasSize ( 1 ) ) );

        verify ( clinicService, times ( 1 ) ).getAllClinics ( );
    }

    @Test
    void when_getClinicInfo_withClinic_thenReturnStatusOk200 () throws Exception {
        UUID clinicId = UUID.randomUUID ( );

        when ( clinicService.getClinicById ( any ( ) ) ).thenReturn ( new ClinicDetails ( ) );

        MockHttpServletRequestBuilder request = get ( "/clinics/clinic/{id}", clinicId );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isOk ( ) )
                .andExpect ( jsonPath ( "id" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "city" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "address" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "pictureUrl" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "phoneNumber" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "workingDays" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "specialties" ).hasJsonPath ( ) );

        verify ( clinicService, times ( 1 ) ).getClinicById ( any ( ) );
    }

    @Test
    void when_getClinicInfo_withNotFoundClinic_thenReturnNotFound404 () throws Exception {
        UUID clinicId = UUID.randomUUID ( );

        when ( clinicService.getClinicById ( any ( ) ) ).thenThrow ( new NoSuchClinicException ( "" ) );

        MockHttpServletRequestBuilder request = get ( "/clinics/clinic/{id}", clinicId );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isNotFound ( ) )
                .andExpect ( jsonPath ( "errorCode" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "messages" ).hasJsonPath ( ) );

        verify ( clinicService, times ( 1 ) ).getClinicById ( any ( ) );
    }

    @Test
    void when_editClinic_withNoJwt_thenReturnUnauthorized401 () throws Exception {
        MockHttpServletRequestBuilder request = put ( "/clinics/clinic/{id}", UUID.randomUUID ( ) );

        mockMvc.perform ( request ).andExpect ( status ( ).isUnauthorized ( ) );

        verify ( clinicService, never ( ) ).updateClinic ( any ( ), any ( ) );
    }

    @Test
    void when_editClinic_withJWTButNoAdmin_thenExpectForbidden403 () throws Exception {
        WorkDayDto workDayDto = WorkDayDto.builder ( )
                .dayName ( "MonDay" )
                .startOfWorkingDay ( LocalTime.of ( 8, 0 ) )
                .endOfWorkingDay ( LocalTime.of ( 17, 0 ) )
                .build ( );

        CreateEditClinicRequest clinicRequest = CreateEditClinicRequest.builder ( )
                .description ( STRING )
                .workingDays ( List.of ( workDayDto ) )
                .pictureUrl ( PICTURE_URL )
                .identificationNumber ( "AA11111111" )
                .phoneNumber ( "1234567890" )
                .city ( STRING )
                .address ( STRING )
                .build ( );

        MockHttpServletRequestBuilder request = put ( "/clinics/clinic/{id}", UUID.randomUUID ( ) )
                .with ( jwt ( )
                        .authorities ( new SimpleGrantedAuthority ( "ROLE_PHYSICIAN" ) )
                )
                .contentType ( MediaType.APPLICATION_JSON )
                .content ( mapper.writeValueAsString ( clinicRequest ) );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isForbidden ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( clinicService, never ( ) ).updateClinic ( any ( ), any ( ) );
    }

    @Test
    void when_editClinic_withValidDto_thenExpectNoContent204 () throws Exception {
        WorkDayDto workDayDto = WorkDayDto.builder ( )
                .dayName ( "MonDay" )
                .startOfWorkingDay ( LocalTime.of ( 8, 0 ) )
                .endOfWorkingDay ( LocalTime.of ( 17, 0 ) )
                .build ( );

        CreateEditClinicRequest clinicRequest = CreateEditClinicRequest.builder ( )
                .description ( STRING )
                .workingDays ( List.of ( workDayDto ) )
                .pictureUrl ( PICTURE_URL )
                .identificationNumber ( "AA11111111" )
                .phoneNumber ( "1234567890" )
                .city ( STRING )
                .address ( STRING )
                .build ( );

        MockHttpServletRequestBuilder request = put ( "/clinics/clinic/{id}", UUID.randomUUID ( ) )
                .with ( jwt ( )
                        .authorities ( new SimpleGrantedAuthority ( "ROLE_ADMIN" ) )
                )
                .contentType ( MediaType.APPLICATION_JSON )
                .content ( mapper.writeValueAsString ( clinicRequest ) );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isNoContent ( ) );

        verify ( clinicService, times ( 1 ) ).updateClinic ( any ( ), any ( ) );
    }

    @Test
    void when_deleteClinic_withNoJwt_thenReturnUnauthorized401 () throws Exception {
        MockHttpServletRequestBuilder request = delete ( "/clinics/clinic/{id}", UUID.randomUUID ( ) );

        mockMvc.perform ( request ).andExpect ( status ( ).isUnauthorized ( ) );

        verify ( clinicService, never ( ) ).deleteClinic ( any ( ) );
    }

    @Test
    void when_deleteClinic_withNotAdmin_thenReturnForbidden403 () throws Exception {
        MockHttpServletRequestBuilder request = delete ( "/clinics/clinic/{id}", UUID.randomUUID ( ) )
                .with (
                        jwt ( ).authorities ( new SimpleGrantedAuthority ( "ROLE_PHYSICIAN" ) )
                );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isForbidden ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( clinicService, never ( ) ).deleteClinic ( any ( ) );
    }

    @Test
    void when_deleteClinic_withNotAdmin_thenReturnNoContent204 () throws Exception {
        MockHttpServletRequestBuilder request = delete ( "/clinics/clinic/{id}", UUID.randomUUID ( ) )
                .with (
                        jwt ( ).authorities ( new SimpleGrantedAuthority ( "ROLE_ADMIN" ) )
                );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isNoContent ( ) );

        verify ( clinicService, times ( 1 ) ).deleteClinic ( any ( ) );
    }

    @Test
    void when_deleteClinic_withNotAdmin_thenReturnNotFound404 () throws Exception {
        doThrow ( new NoSuchClinicException ( "" ) ).when ( clinicService ).deleteClinic ( any ( ) );

        MockHttpServletRequestBuilder request = delete ( "/clinics/clinic/{id}", UUID.randomUUID ( ) )
                .with (
                        jwt ( ).authorities ( new SimpleGrantedAuthority ( "ROLE_ADMIN" ) )
                );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isNotFound ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( clinicService, times ( 1 ) ).deleteClinic ( any ( ) );
    }
}
