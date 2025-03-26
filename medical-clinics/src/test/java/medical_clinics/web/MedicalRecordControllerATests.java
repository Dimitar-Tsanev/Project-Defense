package medical_clinics.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import medical_clinics.patient.model.Patient;
import medical_clinics.patient.service.PatientService;
import medical_clinics.records.exceptions.NoteException;
import medical_clinics.records.service.RecordsService;
import medical_clinics.shared.config.SecurityConfig;
import medical_clinics.web.dto.NewNoteRequest;
import medical_clinics.web.dto.response.NoteResponse;
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

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@WebMvcTest(MedicalRecordController.class)
public class MedicalRecordControllerATests {
    private static final String STRING = "Some random text";
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    RecordsService recordsService;

    @MockitoBean
    PatientService patientService;

    @Test
    void when_addNewNote_withNoJwt_thenExpectUnauthorized401 () throws Exception {
        MockHttpServletRequestBuilder request = post (
                "/medical-records/note/new/physician/{accountId}", UUID.randomUUID ( )
        ).param ( "patientId", UUID.randomUUID ( ).toString ( ) );

        mockMvc.perform ( request ).andExpect ( status ( ).isUnauthorized ( ) );

        verify ( recordsService, never ( ) ).createNote ( any ( ), any ( ), any ( ) );
    }

    @Test
    void when_addNewNote_withNoCredential_thenExpectForbidden403 () throws Exception {
        NewNoteRequest noteRequest = NewNoteRequest.builder ( )
                .diagnosis ( STRING )
                .medicalHistory ( STRING )
                .examination ( STRING )
                .build ( );

        Patient patient = Patient.builder ( )
                .country ( "Some" )
                .identificationCode ( "123456789" )
                .build ( );

        when ( patientService.getPatientById ( any ( ) ) ).thenReturn ( patient );

        MockHttpServletRequestBuilder request = post (
                "/medical-records/note/new/physician/{accountId}", UUID.randomUUID ( )
        ).param (
                "patientId", UUID.randomUUID ( ).toString ( )
        ).with (
                jwt ( ).authorities ( new SimpleGrantedAuthority ( "ROLE_PATIENT" ) )
        ).contentType (
                MediaType.APPLICATION_JSON
        ).content (
                new ObjectMapper ( ).writeValueAsBytes ( noteRequest )
        );

        mockMvc.perform ( request ).andExpect ( status ( ).isForbidden ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( recordsService, never ( ) ).createNote ( any ( ), any ( ), any ( ) );
    }

    @Test
    void when_addNewNote_withAuthority_thenExpectCreated201 () throws Exception {
        NewNoteRequest noteRequest = NewNoteRequest.builder ( )
                .diagnosis ( STRING )
                .medicalHistory ( STRING )
                .examination ( STRING )
                .build ( );

        Patient patient = Patient.builder ( )
                .country ( "Some" )
                .identificationCode ( "123456789" )
                .build ( );

        when ( patientService.getPatientById ( any ( ) ) ).thenReturn ( patient );

        MockHttpServletRequestBuilder request = post (
                "/medical-records/note/new/physician/{accountId}", UUID.randomUUID ( )
        ).param (
                "patientId", UUID.randomUUID ( ).toString ( )
        ).with (
                jwt ( ).authorities ( new SimpleGrantedAuthority ( "ROLE_PHYSICIAN" ) )
        ).contentType (
                MediaType.APPLICATION_JSON
        ).content (
                new ObjectMapper ( ).writeValueAsBytes ( noteRequest )
        );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isCreated ( ) )
                .andExpect ( header ( ).exists ( HttpHeaders.LOCATION ) );

        verify ( recordsService, times ( 1 ) ).createNote ( any ( ), any ( ), any ( ) );
    }

    @Test
    void when_addNewNote_withPatientNotContainingCountryAndIdentificationCode_thenExpectBadRequest400 () throws Exception {
        NewNoteRequest noteRequest = NewNoteRequest.builder ( )
                .diagnosis ( STRING )
                .medicalHistory ( STRING )
                .examination ( STRING )
                .build ( );

        Patient patient = Patient.builder ( )
                .country ( null )
                .identificationCode ( null )
                .build ( );

        when ( patientService.getPatientById ( any ( ) ) ).thenReturn ( patient );

        MockHttpServletRequestBuilder request = post (
                "/medical-records/note/new/physician/{accountId}", UUID.randomUUID ( )
        ).param (
                "patientId", UUID.randomUUID ( ).toString ( )
        ).with (
                jwt ( ).authorities ( new SimpleGrantedAuthority ( "ROLE_PHYSICIAN" ) )
        ).contentType (
                MediaType.APPLICATION_JSON
        ).content (
                new ObjectMapper ( ).writeValueAsBytes ( noteRequest )
        );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isBadRequest ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( recordsService, never ( ) ).createNote ( any ( ), any ( ), any ( ) );
    }

    @Test
    void when_addNewNote_withPatientNotContainingIdentificationCode_thenExpectBadRequest400 () throws Exception {
        NewNoteRequest noteRequest = NewNoteRequest.builder ( )
                .diagnosis ( STRING )
                .medicalHistory ( STRING )
                .examination ( STRING )
                .build ( );

        Patient patient = Patient.builder ( )
                .country ( "Some" )
                .identificationCode ( null )
                .build ( );

        when ( patientService.getPatientById ( any ( ) ) ).thenReturn ( patient );

        MockHttpServletRequestBuilder request = post (
                "/medical-records/note/new/physician/{accountId}", UUID.randomUUID ( )
        ).param (
                "patientId", UUID.randomUUID ( ).toString ( )
        ).with (
                jwt ( ).authorities ( new SimpleGrantedAuthority ( "ROLE_PHYSICIAN" ) )
        ).contentType (
                MediaType.APPLICATION_JSON
        ).content (
                new ObjectMapper ( ).writeValueAsBytes ( noteRequest )
        );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isBadRequest ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( recordsService, never ( ) ).createNote ( any ( ), any ( ), any ( ) );
    }

    @Test
    void when_addNewNote_withPatientNotContainingCountry_thenExpectBadRequest400 () throws Exception {
        NewNoteRequest noteRequest = NewNoteRequest.builder ( )
                .diagnosis ( STRING )
                .medicalHistory ( STRING )
                .examination ( STRING )
                .build ( );

        Patient patient = Patient.builder ( )
                .country ( null )
                .identificationCode ( "AA111111111" )
                .build ( );

        when ( patientService.getPatientById ( any ( ) ) ).thenReturn ( patient );

        MockHttpServletRequestBuilder request = post (
                "/medical-records/note/new/physician/{accountId}", UUID.randomUUID ( )
        ).param (
                "patientId", UUID.randomUUID ( ).toString ( )
        ).with (
                jwt ( ).authorities ( new SimpleGrantedAuthority ( "ROLE_PHYSICIAN" ) )
        ).contentType (
                MediaType.APPLICATION_JSON
        ).content (
                new ObjectMapper ( ).writeValueAsBytes ( noteRequest )
        );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isBadRequest ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( recordsService, never ( ) ).createNote ( any ( ), any ( ), any ( ) );
    }

    @Test
    void when_getPhysicianNotes_withNoAuthentication_thenExpectUnauthorized401 () throws Exception {
        MockHttpServletRequestBuilder request = get (
                "/medical-records/physician/{accountId}", UUID.randomUUID ( )
        );

        mockMvc.perform ( request ).andExpect ( status ( ).isUnauthorized ( ) );

        verify ( recordsService, never ( ) ).getPhysicianNotes ( any ( ) );
    }

    @Test
    void when_getPhysicianNotes_withNoAuthorities_thenExpectForbidden403 () throws Exception {
        MockHttpServletRequestBuilder request = get (
                "/medical-records/physician/{accountId}", UUID.randomUUID ( )
        ).with (
                jwt ( ).authorities ( new SimpleGrantedAuthority ( "ROLE_PATIENT" ) ) );

        mockMvc.perform ( request ).andExpect ( status ( ).isForbidden ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( recordsService, never ( ) ).getPhysicianNotes ( any ( ) );
    }

    @Test
    void when_getPhysicianNotes_withCorrectRequest_thenExpectOk200 () throws Exception {

        when ( recordsService.getPhysicianNotes ( any ( ) ) ).thenReturn ( List.of ( new NoteResponse ( ) ) );

        MockHttpServletRequestBuilder request = get (
                "/medical-records/physician/{accountId}", UUID.randomUUID ( )
        ).with (
                jwt ( ).authorities ( new SimpleGrantedAuthority ( "ROLE_PHYSICIAN" ) )
        );

        mockMvc.perform ( request ).andExpect ( status ( ).isOk ( ) )
                .andExpect ( jsonPath ( "$", hasSize ( 1 ) ) )
                .andExpect ( jsonPath ( "$[0].documentNumber" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "$[0].clinicIdentificationNumber" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "$[0].physicianIdentificationNumber" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "$[0].physicianInfo" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "$[0].patientName" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "$[0].patientFullAddress" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "$[0].patientIdentificationCode" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "$[0].diagnosis" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "$[0].diagnosisCode" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "$[0].chiefComplaint" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "$[0].medicalHistory" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "$[0].examination" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "$[0].medicationAndRecommendations" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "$[0].testResults" ).hasJsonPath ( ) );

        verify ( recordsService, times ( 1 ) ).getPhysicianNotes ( any ( ) );
    }

    @Test
    void when_getPatientRecord_withNoAuthentication_thenExpectUnauthorized401 () throws Exception {
        MockHttpServletRequestBuilder request = get (
                "/medical-records/patient/{patientId}", UUID.randomUUID ( )
        );

        mockMvc.perform ( request ).andExpect ( status ( ).isUnauthorized ( ) );

        verify ( recordsService, never ( ) ).getPatientRecord ( any ( ) );
    }

    @Test
    void when_getPatientRecord_withCorrectRequest_thenExpectOk200 () throws Exception {

        when ( recordsService.getPatientRecord ( any ( ) ) ).thenReturn ( List.of ( new NoteResponse ( ) ) );

        MockHttpServletRequestBuilder request = get (
                "/medical-records/patient/{patientId}", UUID.randomUUID ( )
        ).with (
                jwt ( ).authorities ( new SimpleGrantedAuthority ( "ROLE_PATIENT" ) )
        );

        mockMvc.perform ( request ).andExpect ( status ( ).isOk ( ) )
                .andExpect ( jsonPath ( "$", hasSize ( 1 ) ) )
                .andExpect ( jsonPath ( "$[0].documentNumber" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "$[0].clinicIdentificationNumber" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "$[0].physicianIdentificationNumber" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "$[0].physicianInfo" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "$[0].patientName" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "$[0].patientFullAddress" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "$[0].patientIdentificationCode" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "$[0].diagnosis" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "$[0].diagnosisCode" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "$[0].chiefComplaint" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "$[0].medicalHistory" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "$[0].examination" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "$[0].medicationAndRecommendations" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "$[0].testResults" ).hasJsonPath ( ) );

        verify ( recordsService, times ( 1 ) ).getPatientRecord ( any ( ) );
    }

    @Test
    void when_getNote_withNoAuthentication_thenExpectUnauthorized401 () throws Exception {
        MockHttpServletRequestBuilder request = get (
                "/medical-records/note/{noteId}", UUID.randomUUID ( )
        );

        mockMvc.perform ( request ).andExpect ( status ( ).isUnauthorized ( ) );

        verify ( recordsService, never ( ) ).getNoteById ( any ( ) );
    }

    @Test
    void when_getNote_withCorrectRequest_thenExpectOk200 () throws Exception {
        when ( recordsService.getNoteById ( any ( ) ) ).thenReturn ( new NoteResponse ( ) );

        MockHttpServletRequestBuilder request = get (
                "/medical-records/note/{noteId}", UUID.randomUUID ( )
        ).with (
                jwt ( ).authorities ( new SimpleGrantedAuthority ( "ROLE_PATIENT" ) )
        );


        mockMvc.perform ( request ).andExpect ( status ( ).isOk ( ) )
                .andExpect ( jsonPath ( "documentNumber" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "clinicIdentificationNumber" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "physicianIdentificationNumber" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "physicianInfo" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "patientName" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "patientFullAddress" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "patientIdentificationCode" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "diagnosis" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "diagnosisCode" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "chiefComplaint" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "medicalHistory" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "examination" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "medicationAndRecommendations" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "testResults" ).hasJsonPath ( ) );

        verify ( recordsService, times ( 1 ) ).getNoteById ( any ( ) );
    }

    @Test
    void when_getNote_withNotFoundInMicroservice_thenExpectNotFound404 () throws Exception {
        when ( recordsService.getNoteById ( any ( ) ) ).thenThrow ( new NoteException ( 404,"" ) );

        MockHttpServletRequestBuilder request = get (
                "/medical-records/note/{noteId}", UUID.randomUUID ( )
        ).with (
                jwt ( ).authorities ( new SimpleGrantedAuthority ( "ROLE_PATIENT" ) )
        );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isNotFound ( ) )
                .andExpect ( jsonPath ( "errorCode" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "messages" ).hasJsonPath ( ) );

        verify ( recordsService, times ( 1 ) ).getNoteById ( any ( ) );
    }

    @Test
    void when_getNote_withNotFoundNotConnectedToMicroservice_thenExpectNotFound404 () throws Exception {
        when ( recordsService.getNoteById ( any ( ) ) ).thenThrow ( new NoteException ( "" ) );

        MockHttpServletRequestBuilder request = get (
                "/medical-records/note/{noteId}", UUID.randomUUID ( )
        ).with (
                jwt ( ).authorities ( new SimpleGrantedAuthority ( "ROLE_PATIENT" ) )
        );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isInternalServerError () )
                .andExpect ( jsonPath ( "errorCode" ).hasJsonPath ( ) )
                .andExpect ( jsonPath ( "messages" ).hasJsonPath ( ) );

        verify ( recordsService, times ( 1 ) ).getNoteById ( any ( ) );
    }
}
