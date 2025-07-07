import {Component, OnInit} from '@angular/core';
import {ClinicControllerService} from '../../services/services/clinic-controller.service';
import {FormsModule, NgForm, ReactiveFormsModule} from '@angular/forms';
import {UrlDirective} from '../../utils/validators/url.directive';
import {WorkDayDto} from '../../services/models/work-day-dto';
import {CreateEditClinicRequest} from '../../services/models/create-edit-clinic-request';
import {Router} from '@angular/router';

@Component({
  selector: 'app-add-clinic',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    UrlDirective
  ],
  templateUrl: './add-clinic.component.html',
  styleUrl: './add-clinic.component.css'
})
export class AddClinicComponent implements OnInit {
  workdays: Map<String, WorkDayDto> = new Map();
  days: Set<String> = new Set();

  constructor(
    private clinicService: ClinicControllerService,
    private router: Router,
  ) {
  }

  ngOnInit(): void {
    this.days
      .add('MONDAY')
      .add('TUESDAY')
      .add('WEDNESDAY')
      .add('THURSDAY')
      .add('FRIDAY')
      .add('SATURDAY')
      .add('SUNDAY');
  }


  addClinic(form: NgForm) {
    if (form.invalid) {
      return;
    }

    const clinic: CreateEditClinicRequest = form.value;
    const clinicWorkdays: WorkDayDto[] = [];
    for (let day of this.workdays.values()) {
      clinicWorkdays.push(day);
    }
    clinic.workingDays = clinicWorkdays;

    this.clinicService.addNewClinic$Response({body: clinic}).subscribe({
      next: (res) => {
        const location: string | null = res.headers.get('location');
        if (location) {
          const clinicId = location.split('/').pop();
          this.router.navigate(['/clinic', clinicId]);
        }
      }
    })
  }

  addWorkday(newWorkday: NgForm) {
    if (newWorkday.invalid) {
      return;
    }

    let workday: WorkDayDto = {
      dayName: newWorkday.value.dayName,
      startOfWorkingDay: newWorkday.value.startOfWorkingDay + ':00',
      endOfWorkingDay: newWorkday.value.endOfWorkingDay + ':00'
    }
    this.workdays.set(workday.dayName, workday);
  }

  removeWorkday(dayName: string) {
    this.workdays.delete(dayName);
  }
}
