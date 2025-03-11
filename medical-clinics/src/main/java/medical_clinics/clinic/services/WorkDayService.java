package medical_clinics.clinic.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import medical_clinics.clinic.mappers.WorkDayMapper;
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

    @Transactional
    @Modifying
    public Collection<WorkDay> updateWorkDays ( Collection<WorkDay> oldWorkDays, Collection<WorkDayDto> newWorkDays ) {
        Map<DaysOfWeek, UUID> daysMap = new HashMap<> ( );

        Set<WorkDay> workingDays = new HashSet<> ( );

        oldWorkDays.forEach ( w -> daysMap.put ( w.getDayOfWeek ( ), w.getId ( ) ) );

        newWorkDays.forEach ( w -> {
            WorkDay day = WorkDayMapper.mapToModel ( w );

            if ( daysMap.containsKey ( day.getDayOfWeek ( ) ) ) {
                day.setId ( daysMap.get ( day.getDayOfWeek ( ) ) );
            }

            workDaysRepository.save ( day );
            workingDays.add ( day );
        } );
        return workingDays;
    }

}
