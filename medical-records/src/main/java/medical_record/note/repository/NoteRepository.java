package medical_record.note.repository;

import medical_record.note.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {
    List<Note> findAllByPatientId ( UUID patientId );

    List<Note> findAllByPhysicianId ( UUID physicianId );
}
