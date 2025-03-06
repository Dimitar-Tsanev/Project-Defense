package medical_clinics.clinic.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import medical_clinics.clinic.models.Clinic;
import medical_clinics.clinic.models.WorkDay;
import medical_clinics.clinic.repositories.ClinicRepository;
import medical_clinics.physician.model.Physician;
import medical_clinics.shared.exception.ExistingClinicException;
import medical_clinics.shared.exception.NoSuchClinicException;
import medical_clinics.shared.mappers.ClinicMapper;
import medical_clinics.specialty.model.Specialty;
import medical_clinics.web.dto.*;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.util.*;
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
        return ClinicMapper.mapClinicToClinicDetails ( getById ( id ) );
    }

    @Transactional
    public void upsertClinic (CreateEditClinicRequest clinic){
        if ( clinic.getClinicId () != null ) {
                updateClinic ( clinic );
        }

        addClinic ( clinic );
    }

    @Transactional
    public void deleteClinic ( UUID id ) {
        clinicRepository.deleteById ( id );
    }


    public Clinic getClinicIdByCityAndAddress ( String city, String address ) {
        return clinicRepository.findByCityAndAddress ( city, address )
                .orElseThrow ( () ->
                        new NoSuchClinicException (
                                "Clinic in [%s] on [%s] dose not exists"
                                        .formatted ( city, address )
                        )
                );
    }

    @EventListener
    void removeSpecialityIfNoPhysicianEmployed ( NoSpecialistsLeftEvent noSpecialists ) {
        Clinic clinic = getById ( noSpecialists.getClinicId ( ) );

        UUID specialityToRemove = noSpecialists.getSpecialtyId ( );

        Set<Specialty> specialitiesLeft = clinic.getSpecialties ()
                .stream( )
                .filter ( specialty -> !specialty.getId ().equals ( specialityToRemove ) )
                .collect( Collectors.toSet());

        clinic.setSpecialties ( specialitiesLeft );
        clinicRepository.save ( clinic );
    }

    public void addPhysicianSpeciality ( Physician newPhysician ) {
        Clinic clinic = newPhysician.getWorkplace ( );

        Specialty specialty = newPhysician.getSpecialty ( );

        boolean containsSpeciality = clinic
                .getSpecialties ( )
                .contains ( specialty );

        if ( !containsSpeciality ) {
            clinic.addSpeciality ( specialty );
        }
        clinicRepository.save ( clinic );
    }

    private void addClinic ( CreateEditClinicRequest clinic ) {
        Clinic newClinic = ClinicMapper.mapToClinic ( clinic );

        Optional<Clinic> exists = clinicRepository.findByCityAndAddress ( clinic.getCity ( ), clinic.getAddress ( ) );

        if ( exists.isPresent ( ) ) {
            throw new ExistingClinicException ( "Clinic in same city and address already exists" );
        }

        clinicRepository.save ( newClinic );
    }

    @Modifying
    private void updateClinic ( CreateEditClinicRequest clinic ) {
        Optional<Clinic> exists = clinicRepository.findById ( clinic.getClinicId () );

        if ( exists.isEmpty ( ) ) {
            throw new NoSuchClinicException ( "Clinic with provided id does not exist" );
        }

        Clinic oldClinicInfo = exists.get ( );

        Collection<WorkDay> workDays = workDayService.updateWorkDays (
                oldClinicInfo.getWorkingDays ( ), clinic.getWorkingDays ( )
        );

        Clinic newClinicInfo = ClinicMapper.mapToClinic ( clinic );
        newClinicInfo.setId ( oldClinicInfo.getId ( ) );
        newClinicInfo.setWorkingDays ( workDays );
        newClinicInfo.setSpecialties ( oldClinicInfo.getSpecialties ( ) );

        clinicRepository.save ( newClinicInfo );
    }

    private Clinic getById ( UUID id ) {
        return clinicRepository.findById ( id )
                .orElseThrow ( () -> new NoSuchClinicException (
                        "Clinic with provided id does not exist" )
                );
    }
}
