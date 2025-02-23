package medical_clinics.schedule.services;

import lombok.AllArgsConstructor;
import medical_clinics.schedule.models.Status;
import medical_clinics.schedule.models.TimeSlot;
import medical_clinics.schedule.repositories.TimeSlotRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class TimeSlotService {
    private TimeSlotRepository timeSlotRepository;

    public Collection<TimeSlot> generateTimeSlots (
            LocalTime scheduleStartTime,
            LocalTime scheduleEndTime,
            Integer timeSlotInterval ) {

        List<TimeSlot> timeSlots = new ArrayList<> ();
        LocalTime startTime = scheduleStartTime;

        while (!startTime.isAfter ( scheduleEndTime )) {
            TimeSlot timeSlot = TimeSlot.builder ( )
                    .startTime ( startTime )
                    .durationInMinutes ( timeSlotInterval )
                    .status ( Status.FREE )
                    .build ( );

            timeSlots.add ( timeSlot );
            startTime = startTime.plusMinutes(timeSlotInterval);
        }
        return timeSlotRepository.saveAll ( timeSlots );
    }
}
