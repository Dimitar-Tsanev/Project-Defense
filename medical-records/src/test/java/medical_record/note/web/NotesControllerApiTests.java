package medical_record.note.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import medical_record.note.service.NoteService;
import medical_record.note.util.NoteNotFound;
import medical_record.note.web.dtos.NoteDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotesController.class)
public class NotesControllerApiTests {

    @MockitoBean
    NoteService noteService;

    @Autowired
    MockMvc mockMvc;

    @Test
    void when_createNote_WithInvalidDiagnosis_expectStatusBadRequest400 () throws Exception {
        ObjectMapper mapper = new ObjectMapper ( );
        mapper.findAndRegisterModules ( );

        MockHttpServletRequestBuilder request = post ( "/notes" )
                .contentType ( MediaType.APPLICATION_JSON )
                .content ( mapper.writeValueAsBytes ( TestNotesDtoBuilder.buildInvalidPatternDiagnosisImport ( ) ) );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isBadRequest ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( noteService, never ( ) ).addNote ( any ( ) );
    }

    @Test
    void when_createNote_WithInvalidDiagnosisCode_expectStatusBadRequest400 () throws Exception {
        ObjectMapper mapper = new ObjectMapper ( );
        mapper.findAndRegisterModules ( );

        MockHttpServletRequestBuilder request = post ( "/notes" )
                .contentType ( MediaType.APPLICATION_JSON )
                .content ( mapper.writeValueAsBytes ( TestNotesDtoBuilder.buildInvalidPatternDiagnosisCodeImport ( ) ) );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isBadRequest ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( noteService, never ( ) ).addNote ( any ( ) );
    }

    @Test
    void when_createNote_WithInvalidNullValues_expectStatusBadRequest400 () throws Exception {
        ObjectMapper mapper = new ObjectMapper ( );
        mapper.findAndRegisterModules ( );

        MockHttpServletRequestBuilder request = post ( "/notes" )
                .contentType ( MediaType.APPLICATION_JSON )
                .content ( mapper.writeValueAsBytes ( TestNotesDtoBuilder.buildInvalidNullParametersImport ( ) ) );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isBadRequest ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( noteService, never ( ) ).addNote ( any ( ) );
    }

    @Test
    void when_createNote_WithInvalidBlankValues_expectStatusBadRequest400 () throws Exception {
        ObjectMapper mapper = new ObjectMapper ( );
        mapper.findAndRegisterModules ( );

        MockHttpServletRequestBuilder request = post ( "/notes" )
                .contentType ( MediaType.APPLICATION_JSON )
                .content ( mapper.writeValueAsBytes ( TestNotesDtoBuilder.buildInvalidBlankParametersImport ( ) ) );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isBadRequest ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( noteService, never ( ) ).addNote ( any ( ) );
    }

    @Test
    void when_createNote_WithValidData_expectStatusCreated201 () throws Exception {
        ObjectMapper mapper = new ObjectMapper ( );
        mapper.findAndRegisterModules ( );

        NoteDto dto = TestNotesDtoBuilder.buildValidImport ( );
        UUID noteId = UUID.randomUUID ( );

        MockHttpServletRequestBuilder request = post ( "/notes" )
                .contentType ( MediaType.APPLICATION_JSON )
                .content ( mapper.writeValueAsBytes ( dto ) );

        when ( noteService.addNote ( dto ) ).thenReturn ( noteId );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isCreated ( ) )
                .andExpect ( header ( ).exists ( HttpHeaders.LOCATION ) );

        verify ( noteService, times ( 1 ) ).addNote ( any ( NoteDto.class ) );
    }

    @Test
    void when_unknownException_expectStatusInternalServerError500 () throws Exception {
        when ( noteService.getNoteById ( any ( ) ) ).thenThrow ( new RuntimeException ( "text" ) );

        MockHttpServletRequestBuilder request = get ( "/notes/{id}", UUID.randomUUID ( ).toString ( ) )
                .contentType ( MediaType.APPLICATION_JSON );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isInternalServerError ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( noteService, times ( 1 ) ).getNoteById ( any ( ) );
    }

    @Test
    void getNoteById_expectStatusNotFound404 () throws Exception {
        when ( noteService.getNoteById ( any ( ) ) ).thenThrow ( new NoteNotFound ("Note not found" ) );

        MockHttpServletRequestBuilder request = get ( "/notes/{id}", UUID.randomUUID ( ).toString ( ) )
                .contentType ( MediaType.APPLICATION_JSON );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isNotFound () )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( noteService, times ( 1 ) ).getNoteById ( any ( ) );
    }

    @Test
    void when_getNoteById_expectStatusOk200 () throws Exception {
        when ( noteService.getNoteById ( any () ) ).thenReturn ( TestNotesDtoBuilder.buildExport ( ) );

        MockHttpServletRequestBuilder request = get ( "/notes/{id}", UUID.randomUUID ( ).toString ( ) )
                .contentType ( MediaType.APPLICATION_JSON );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isOk ( ) )
                .andExpect ( jsonPath ( "noteId" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "documentNumber" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "creationDate" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "clinicIdentificationNumber" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "patientId" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "physicianId" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "diagnosis" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "diagnosisCode" ).hasJsonPath () )
                .andExpect ( jsonPath ( "chiefComplaint" ).hasJsonPath () )
                .andExpect ( jsonPath ( "medicalHistory" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "examination" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "medicationAndRecommendations" ).hasJsonPath () )
                .andExpect ( jsonPath ( "testResults" ).hasJsonPath () );

        verify ( noteService, times ( 1 ) ).getNoteById ( any ( ) );
    }

    @Test
    void when_getPatientMedicalRecord_WithoutFoundPatient_expectStatusOk200 () throws Exception {
        when ( noteService.getPatientMedicalRecord ( any () ) ).thenReturn ( new ArrayList<> () );

        MockHttpServletRequestBuilder request = get ("/notes/patient/{id}", UUID.randomUUID ( ).toString ( ) );
        mockMvc.perform ( request )
                .andExpect ( status ( ).isOk ( ) )
                .andExpect ( jsonPath ( "$" ,hasSize(0)) );

        verify ( noteService, times ( 1 ) ).getPatientMedicalRecord ( any ( ) );
    }

    @Test
    void when_getPatientMedicalRecord_WithFoundPatient_expectStatusOk200 () throws Exception {
        when ( noteService.getPatientMedicalRecord ( any () ) ).thenReturn ( List.of ( new NoteDto (), new NoteDto () ) );

        MockHttpServletRequestBuilder request = get ("/notes/patient/{id}", UUID.randomUUID ( ).toString ( ) );
        mockMvc.perform ( request )
                .andExpect ( status ( ).isOk ( ) )
                .andExpect ( jsonPath ( "$" ,hasSize(2)) );

        verify ( noteService, times ( 1 ) ).getPatientMedicalRecord ( any ( ) );
    }

    @Test
    void when_getPhysicianNotes_WithoutFoundPhysician_expectStatusOk200 () throws Exception {
        when ( noteService.getPhysicianNotes ( any () ) ).thenReturn ( new ArrayList<> () );

        MockHttpServletRequestBuilder request = get ("/notes/physician/{id}", UUID.randomUUID ( ).toString ( ) );
        mockMvc.perform ( request )
                .andExpect ( status ( ).isOk ( ) )
                .andExpect ( jsonPath ( "$" ,hasSize(0)) );

        verify ( noteService, times ( 1 ) ).getPhysicianNotes ( any ( ) );
    }

    @Test
    void when_getPhysicianNotes_WithFoundPhysician_expectStatusOk200 () throws Exception {
        when ( noteService.getPhysicianNotes ( any () ) ).thenReturn ( List.of ( new NoteDto (), new NoteDto () ) );

        MockHttpServletRequestBuilder request = get ("/notes/physician/{id}", UUID.randomUUID ( ).toString ( ) );
        mockMvc.perform ( request )
                .andExpect ( status ( ).isOk ( ) )
                .andExpect ( jsonPath ( "$" ,hasSize(2)) );

        verify ( noteService, times ( 1 ) ).getPhysicianNotes ( any ( ) );
    }
}
