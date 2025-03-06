package medical_record.note.util;

import medical_record.note.model.Note;
import medical_record.note.web.dtos.NoteDto;

public class NoteMapper {
    private NoteMapper() {
    }

    public static Note mapFromDto( NoteDto noteDto) {
        return Note.builder()
                .patientId ( noteDto.getPatientId() )
                .physicianId ( noteDto.getPhysicianId() )
                .diagnosis ( noteDto.getDiagnosis() )
                .diagnosisCode ( noteDto.getDiagnosisCode() )
                .chiefComplaint ( noteDto.getChiefComplaint() )
                .medicalHistory ( noteDto.getMedicalHistory() )
                .examination ( noteDto.getExamination() )
                .medicationAndRecommendations ( noteDto.getMedicationAndRecommendations() )
                .testResults( noteDto.getTestResults() )
                .build();
    }

    public static NoteDto mapToDto( Note note) {
        return NoteDto.builder()
                .noteId ( note.getId() )
                .patientId ( note.getPatientId() )
                .physicianId ( note.getPhysicianId() )
                .diagnosis ( note.getDiagnosis() )
                .diagnosisCode ( note.getDiagnosisCode() )
                .chiefComplaint ( note.getChiefComplaint() )
                .medicalHistory ( note.getMedicalHistory() )
                .examination ( note.getExamination() )
                .medicationAndRecommendations ( note.getMedicationAndRecommendations() )
                .testResults( note.getTestResults() )
                .build();
    }
}
