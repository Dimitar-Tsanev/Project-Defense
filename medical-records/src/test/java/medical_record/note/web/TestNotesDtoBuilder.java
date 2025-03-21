package medical_record.note.web;

import medical_record.note.web.dtos.NoteDto;

import java.time.LocalDate;
import java.util.UUID;

public class TestNotesDtoBuilder {
    private static final String NUMBER = "123456789";
    private static final LocalDate DATE = LocalDate.of ( 2025, 3, 21 );
    private static final String SOME = "SomeText";
    private static final UUID ID = UUID.randomUUID ( );
    private static final String DIAGNOSIS_CODE_VALID = "F30.9";
    private static final String DIAGNOSIS_CODE_INVALID = "AA30.9";

    private TestNotesDtoBuilder() {}

    public static NoteDto buildExport () {
        return NoteDto.builder ( )
                .noteId ( ID )
                .documentNumber ( NUMBER )
                .clinicIdentificationNumber ( NUMBER )
                .creationDate ( DATE )
                .patientId ( ID )
                .physicianId ( ID )
                .diagnosis ( SOME )
                .diagnosisCode ( DIAGNOSIS_CODE_VALID )
                .chiefComplaint ( SOME )
                .medicalHistory ( SOME )
                .examination ( SOME )
                .medicationAndRecommendations ( null )
                .testResults ( null )
                .build ( );
    }

    public static NoteDto buildValidImport (UUID patientId, UUID physicianId) {
        return NoteDto.builder ( )
                .documentNumber ( NUMBER )
                .clinicIdentificationNumber ( NUMBER )
                .creationDate ( DATE )
                .patientId ( patientId )
                .physicianId ( physicianId )
                .diagnosis ( SOME )
                .diagnosisCode ( DIAGNOSIS_CODE_VALID )
                .chiefComplaint ( SOME )
                .medicalHistory ( SOME )
                .examination ( SOME )
                .medicationAndRecommendations ( null )
                .testResults ( null )
                .build ( );
    }

    public static NoteDto buildValidImport () {
        return NoteDto.builder ( )
                .documentNumber ( NUMBER )
                .clinicIdentificationNumber ( NUMBER )
                .creationDate ( DATE )
                .patientId ( ID )
                .physicianId ( ID )
                .diagnosis ( SOME )
                .diagnosisCode ( DIAGNOSIS_CODE_VALID )
                .chiefComplaint ( SOME )
                .medicalHistory ( SOME )
                .examination ( SOME )
                .medicationAndRecommendations ( null )
                .testResults ( null )
                .build ( );
    }

    public static NoteDto buildInvalidBlankParametersImport () {
        return NoteDto.builder ( )
                .documentNumber ( " " )
                .clinicIdentificationNumber ( " " )
                .creationDate ( DATE )
                .patientId ( ID )
                .physicianId ( ID )
                .diagnosis ( " " )
                .diagnosisCode ( DIAGNOSIS_CODE_VALID )
                .chiefComplaint ( SOME )
                .medicalHistory ( " " )
                .examination ( " " )
                .medicationAndRecommendations ( null )
                .testResults ( null )
                .build ( );
    }

    public static NoteDto buildInvalidNullParametersImport () {
        return NoteDto.builder ( )
                .documentNumber ( null )
                .clinicIdentificationNumber ( null )
                .creationDate ( null )
                .patientId ( null )
                .physicianId ( null )
                .diagnosis ( null )
                .diagnosisCode ( null )
                .chiefComplaint ( null )
                .medicalHistory ( null )
                .examination ( null )
                .medicationAndRecommendations ( null )
                .testResults ( null )
                .build ( );
    }

    public static NoteDto buildInvalidPatternDiagnosisCodeImport () {
        return NoteDto.builder ( )
                .documentNumber ( NUMBER )
                .clinicIdentificationNumber ( NUMBER )
                .creationDate ( DATE )
                .patientId ( ID )
                .physicianId ( ID )
                .diagnosis ( SOME )
                .diagnosisCode ( DIAGNOSIS_CODE_INVALID )
                .chiefComplaint ( SOME )
                .medicalHistory ( SOME )
                .examination ( SOME )
                .medicationAndRecommendations ( null )
                .testResults ( null )
                .build ( );
    }

    public static NoteDto buildInvalidPatternDiagnosisImport () {
        return NoteDto.builder ( )
                .documentNumber ( NUMBER )
                .clinicIdentificationNumber ( NUMBER )
                .creationDate ( DATE )
                .patientId ( ID )
                .physicianId ( ID )
                .diagnosis ( SOME + "?" )
                .diagnosisCode ( DIAGNOSIS_CODE_VALID )
                .chiefComplaint ( SOME )
                .medicalHistory ( SOME )
                .examination ( SOME )
                .medicationAndRecommendations ( null )
                .testResults ( null )
                .build ( );
    }
}
