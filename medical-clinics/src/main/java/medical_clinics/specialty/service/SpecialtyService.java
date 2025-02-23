package medical_clinics.specialty.service;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import medical_clinics.shared.exception.SpecialityException;
import medical_clinics.shared.mappers.SpecialityMapper;
import medical_clinics.specialty.model.Specialty;
import medical_clinics.specialty.model.SpecialtyName;
import medical_clinics.specialty.repository.SpecialtyRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class SpecialtyService {
    private SpecialtyRepository specialtyRepository;

    @PostConstruct
    private void onInit () {
        if ( specialtyRepository.count ( ) == 0 ) {
            for ( SpecialtyName specialtyName : SpecialtyName.values ( ) ) {
                specialtyRepository.save ( new Specialty ( specialtyName ) );
            }
        }
    }

    public Specialty getSpecialtyByName ( String specialtyName ) {
        SpecialtyName specialty = SpecialityMapper.mapNameFromString ( specialtyName );
        Optional<Specialty> speciality = specialtyRepository.getByName ( specialty );

        if ( speciality.isPresent ( ) ) {
            return speciality.get ( );
        }
        throw new SpecialityException ( "Specialty with provided name does not exist" );
    }
}
