package medical_clinics.specialty.service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import medical_clinics.physician.model.Physician;
import medical_clinics.shared.exception.SpecialityException;
import medical_clinics.specialty.mapper.SpecialityMapper;
import medical_clinics.specialty.model.Specialty;
import medical_clinics.specialty.model.SpecialtyName;
import medical_clinics.specialty.repository.SpecialtyRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SpecialtyService {
    private SpecialtyRepository specialtyRepository;

    @PostConstruct
    private void onInit(){
        if(specialtyRepository.count() == 0){
            for ( SpecialtyName specialtyName : SpecialtyName.values() ){
                specialtyRepository.save(new Specialty (specialtyName));
            }
        }
    }

    @Transactional
    public Specialty addPhysician( UUID id, Physician physician ){
        Specialty specialty = specialtyRepository.findById ( id )
                .orElseThrow (() ->
                        new SpecialityException ( "Speciality with provided id does not exist" ) );

        specialty.addPhysician(physician);
        return specialtyRepository.save(specialty);
    };

    public UUID getIdBySpecialtyName ( String specialtyName ){
        SpecialtyName specialty = SpecialityMapper.mapNameFromString ( specialtyName );
        Optional<Specialty> speciality = specialtyRepository.getByName ( specialty );

        if ( speciality.isPresent() ){
            return speciality.get().getId();
        }
        throw new SpecialityException ( "Specialty with provided name does not exist" );
    }
}
