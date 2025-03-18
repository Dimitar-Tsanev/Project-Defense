package medical_clinics.clinic.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import medical_clinics.clinic.mappers.WorkDayMapper;
import medical_clinics.clinic.models.Clinic;
import medical_clinics.clinic.models.DaysOfWeek;
import medical_clinics.clinic.models.WorkDay;
import medical_clinics.clinic.repositories.WorkDaysRepository;
import medical_clinics.web.dto.WorkDayDto;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class WorkDayService {
    private final WorkDaysRepository workDaysRepository;

    public Collection<WorkDay> addWorkdays ( Collection<WorkDayDto> workdays, Clinic clinic ) {
        Map<DaysOfWeek, WorkDay> daysMap = new HashMap<> ( );

        workdays.stream ()
                .map ( WorkDayMapper::mapToModel )
                .forEach( workday -> daysMap.put( workday.getDayOfWeek ( ) , workday ));

        List<WorkDay> clinicWorkdays = new ArrayList<> ( );

        for ( WorkDay workday : daysMap.values ()) {
            workday.setClinic ( clinic );
            clinicWorkdays.add ( workDaysRepository.save ( workday ) );
        }

        return clinicWorkdays;
    }

    @Transactional
    @Modifying
    public Collection<WorkDay> updateWorkDays ( Clinic clinic, Collection<WorkDayDto> newWorkDays ) {
        Map<DaysOfWeek, WorkDay> daysMap = new HashMap<> ( );

        List<WorkDay> workingDays = new ArrayList<> ( );

        workDaysRepository.findAllByClinic(clinic)
                .forEach ( w -> daysMap.put ( w.getDayOfWeek ( ), w ) );

        newWorkDays.forEach ( w -> {
            WorkDay day = WorkDayMapper.mapToModel ( w );
            DaysOfWeek dayOfWeek = day.getDayOfWeek ( );

            if ( daysMap.containsKey ( dayOfWeek ) ) {
                daysMap.get ( dayOfWeek ).setStartOfWorkingDay ( day.getStartOfWorkingDay ( ) ) ;
                daysMap.get ( dayOfWeek ).setEndOfWorkingDay ( day.getEndOfWorkingDay ( ) ) ;

                day = daysMap.get ( dayOfWeek );

                daysMap.remove ( dayOfWeek );
            }

            day.setClinic ( clinic );
            workDaysRepository.save ( day );
            workingDays.add ( day );
        } );

        if ( !daysMap.isEmpty () ) {
            workDaysRepository.deleteAll ( daysMap.values ( ) );
        }

        return workingDays;
    }

}
