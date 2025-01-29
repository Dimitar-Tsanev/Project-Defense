package medical_clinics.medical_record_note.repository;

import medical_clinics.medical_record_note.model.MedicalRecordNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MedicalRecordNoteRepository  extends JpaRepository<MedicalRecordNote, UUID> {
}
