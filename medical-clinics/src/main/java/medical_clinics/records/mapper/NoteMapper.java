package medical_clinics.records.mapper;

import medical_clinics.patient.model.Patient;
import medical_clinics.physician.model.Physician;
import medical_clinics.records.client.dto.NoteDto;
import medical_clinics.web.dto.NewNoteRequest;
import medical_clinics.web.dto.response.NoteResponse;

import java.time.LocalDate;
import java.util.UUID;

public class NoteMapper {
    private NoteMapper () {
    }

    public static NoteDto mapToNoteDto ( UUID physicianId, UUID patientId,
                                         String documentNumber, LocalDate createdOn,
                                         String clinicIdentification, NewNoteRequest note ) {

        return NoteDto.builder ( )
                .patientId ( patientId )
                .documentNumber ( documentNumber )
                .creationDate ( createdOn )
                .clinicIdentificationNumber ( clinicIdentification )
                .physicianId ( physicianId )
                .chiefComplaint ( note.getChiefComplaint ( ) )
                .examination ( note.getExamination ( ) )
                .diagnosis ( note.getDiagnosis ( ) )
                .diagnosisCode ( note.getDiagnosisCode ( ) )
                .medicalHistory ( note.getMedicalHistory ( ) )
                .testResults ( note.getTestResults ( ) )
                .medicationAndRecommendations ( note.getMedicationAndRecommendations ( ) )
                .build ( );
    }

    public static NoteResponse mapToNoteResponse ( Physician physician, Patient patient, NoteDto dto ) {
        String specialtyName = physician.getSpecialty ( ).getName ( ).name ( );

        String specialtyNameFormatted = specialtyName.charAt ( 0 ) +
                specialtyName.substring ( 1 ).toLowerCase ( );

        return NoteResponse.builder ( )
                .documentNumber ( dto.getDocumentNumber ( ) )
                .creationDate ( dto.getCreationDate ( ) )
                .clinicIdentificationNumber ( dto.getClinicIdentificationNumber ( ) )
                .physicianIdentificationNumber ( physician.getIdentificationNumber ( ) )
                .physicianInfo (
                        physician.getFirstName ( ) + " " +
                                physician.getLastName ( ) + "-" +
                                physician.getAbbreviation ( ) + ", " +
                                specialtyNameFormatted
                )
                .patientName ( patient.getFirstName ( ) + " " + patient.getLastName ( ) )
                .patientFullAddress (
                        patient.getCountry ( ) + ", " +
                                patient.getCity ( ) + ", " +
                                patient.getAddress ( )
                )
                .patientIdentificationCode ( patient.getIdentificationCode ( ) )
                .diagnosis ( dto.getDiagnosis ( ) )
                .diagnosisCode ( dto.getDiagnosisCode ( ) )
                .chiefComplaint ( dto.getChiefComplaint ( ) )
                .medicalHistory ( dto.getMedicalHistory ( ) )
                .examination ( dto.getExamination ( ) )
                .medicationAndRecommendations ( dto.getMedicationAndRecommendations ( ) )
                .testResults ( dto.getTestResults ( ) )
                .build ( );
    }
}
