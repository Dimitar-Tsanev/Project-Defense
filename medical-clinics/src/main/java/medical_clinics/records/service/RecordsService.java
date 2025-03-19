package medical_clinics.records.service;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medical_clinics.patient.model.Patient;
import medical_clinics.patient.service.PatientService;
import medical_clinics.physician.model.Physician;
import medical_clinics.physician.service.PhysicianService;
import medical_clinics.records.client.MedicalRecordsClient;
import medical_clinics.records.client.dto.NoteDto;
import medical_clinics.records.exceptions.NoteException;
import medical_clinics.records.mapper.NoteMapper;
import medical_clinics.web.dto.NewNoteRequest;
import medical_clinics.web.dto.response.NoteResponse;
import medical_clinics.web.exception_handler.ExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Slf4j

@Service
public class RecordsService {
    private final MedicalRecordsClient medicalRecordsClient;
    private final PhysicianService physicianService;
    private final PatientService patientService;

    private static int counter;

    @Transactional
    public UUID createNote ( UUID physicianAccountId, UUID patientId, NewNoteRequest noteDto ) {
        UUID physicianId = physicianService.getPhysicianIdByUserAccountId ( physicianAccountId );
        Physician physician = physicianService.getPhysicianById ( physicianId );

        counter++;

        ResponseEntity<String> response = medicalRecordsClient.createNote (
                NoteMapper.mapToNoteDto (
                        physician.getId ( ),
                        patientId, createDocumentNumber ( ),
                        LocalDate.now ( ),
                        physician.getWorkplace ( ).getIdentificationNumber ( ),
                        noteDto
                )
        );

        if ( !response.getStatusCode ( ).is2xxSuccessful ( ) ) {
            throw new NoteException ( response.getStatusCode ( ).value ( ),
                    parseErrorMessage ( response.getBody ( ) )
            );
        }

        String location = response.getHeaders ( ).getLocation ( ).toString ( );
        String[] linkParts = location.split ( "/" );

        return UUID.fromString ( linkParts[linkParts.length - 1] );
    }

    @Transactional
    public NoteResponse getNoteById ( UUID noteId ) {
        ResponseEntity<NoteDto> response = medicalRecordsClient.getNote ( noteId );

        if ( !response.getStatusCode ( ).is2xxSuccessful ( ) ) {
            throw new NoteException ( response.getStatusCode ( ).value ( ),
                    parseErrorMessage ( response.getBody ( ).toString ( ) )
            );
        }
        return mapToResponse ( response.getBody ( ) );
    }

    @Transactional
    public List<NoteResponse> getPatientRecord ( UUID patientId ) {
        Patient patient = patientService.getPatientById ( patientId );

        ResponseEntity<Collection<NoteDto>> recordResponse =
                medicalRecordsClient.getPatientRecord ( patient.getId ( ) );

        if ( recordResponse.getStatusCode ( ).is2xxSuccessful ( ) ) {
            return recordResponse.getBody ( ).stream ( ).map ( this::mapToResponse ).toList ( );
        }

        log.error ( "Feign call failed. Error getting patient record for patient id: {}", patient.getId ( ) );
        throw new NoteException ( "Cant load patient record" );
    }

    @Transactional
    public List<NoteResponse> getPhysicianNotes ( UUID physicianAccountId ) {
        UUID physicianId = physicianService.getPhysicianIdByUserAccountId ( physicianAccountId );

        ResponseEntity<Collection<NoteDto>> recordResponse =
                medicalRecordsClient.getPhysicianNotes ( physicianId );

        if ( recordResponse.getStatusCode ( ).is2xxSuccessful ( ) ) {
            return recordResponse.getBody ( ).stream ( ).map ( this::mapToResponse ).toList ( );
        }

        log.error ( "Feign call failed. Error getting physician notes for physician id: {}", physicianId );
        throw new NoteException ( "Cant load physician notes" );
    }

    private NoteResponse mapToResponse ( NoteDto dto ) {
        Physician physician = physicianService.getPhysicianById ( dto.getPhysicianId ( ) );
        Patient patient = patientService.getPatientById ( dto.getPatientId ( ) );

        return NoteMapper.mapToNoteResponse ( physician, patient, dto );
    }

    private String createDocumentNumber () {
        String dayNumber = LocalDate.now ( )
                .format ( DateTimeFormatter.ofPattern ( "yyMMdd" ) );

        String number = String.format ( "%04d", counter );

        return dayNumber + number;
    }

    private String parseErrorMessage ( String jsonBody ) {
        if ( jsonBody == null || jsonBody.isBlank ( ) ) {
            return jsonBody;
        }

        Gson gson = new GsonBuilder ( )
                .setPrettyPrinting ( )
                .excludeFieldsWithoutExposeAnnotation ( )
                .create ( );

        return String.join ( "%n",
                gson.fromJson ( jsonBody, ExceptionResponse.class )
                        .getMessages ( ) );
    }

    @Scheduled(cron = "0 00 00 * * *")
    void counterReset () {
        counter = 0;
    }
}
