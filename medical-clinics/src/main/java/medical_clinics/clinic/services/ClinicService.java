package medical_clinics.clinic.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import medical_clinics.clinic.mappers.ClinicMapper;
import medical_clinics.clinic.models.Clinic;
import medical_clinics.clinic.models.WorkDay;
import medical_clinics.clinic.repositories.ClinicRepository;
import medical_clinics.shared.exception.ExistingClinicException;
import medical_clinics.shared.exception.NoSuchClinicException;
import medical_clinics.web.dto.ClinicDetails;
import medical_clinics.web.dto.ClinicShortInfo;
import medical_clinics.web.dto.NewClinicRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ClinicService {

    private final ClinicRepository clinicRepository;
    private final WorkDayService workDayService;

    public List<ClinicShortInfo> getAllClinics () {
        return clinicRepository.findAll ( )
                .stream ( )
                .map ( ClinicMapper::mapClinicToClinicShortInfo )
                .collect ( Collectors.toList ( ) );
    }

    public ClinicDetails getClinicById ( UUID id ) {
        Clinic clinic = clinicRepository.findById ( id )
                .orElseThrow ( () -> new NoSuchClinicException ( "Clinic with provided id does not exist" ) );

        return ClinicMapper.mapClinicToClinicDetails ( clinic );
    }

    @Transactional
    public void addClinic ( NewClinicRequest clinic ) {
        Clinic newClinic = ClinicMapper.mapToClinic ( clinic );

        Optional<Clinic> exists = clinicRepository.findByCityAndAddress ( clinic.getCity ( ), clinic.getAddress ( ) );

        if ( exists.isPresent ( ) ) {
            throw new ExistingClinicException ( "Clinic in same city and address already exists" );
        }

        clinicRepository.save ( newClinic );
    }

    @Transactional
    @Modifying
    public void updateClinic ( UUID id, NewClinicRequest clinic ) {
        Optional<Clinic> exists = clinicRepository.findById ( id );

        if ( exists.isEmpty ( ) ) {
            throw new NoSuchClinicException ( "Clinic with provided id does not exist" );
        }

        Clinic oldClinicInfo = exists.get ( );

        Collection<WorkDay> workDays = workDayService.updateWorkDays (
                oldClinicInfo.getWorkingDays (), clinic.getWorkingDays ()
        );
        Clinic newClinicInfo = ClinicMapper.mapToClinic ( clinic );
        newClinicInfo.setId ( oldClinicInfo.getId () );
        newClinicInfo.setWorkingDays (workDays);
        newClinicInfo.setPhysicians ( oldClinicInfo.getPhysicians () );
        newClinicInfo.setSpecialtyList ( oldClinicInfo.getSpecialtyList () );

        clinicRepository.save ( newClinicInfo );
    }

    public void deleteClinic ( UUID id ) {
        clinicRepository.deleteById ( id );
    }
}
