import {Component, OnInit} from '@angular/core';
import {ClinicControllerService} from '../../services/services/clinic-controller.service';
import {FormsModule, NgForm, ReactiveFormsModule} from '@angular/forms';
import {UrlDirective} from '../../utils/validators/url.directive';
import {WorkDayDto} from '../../services/models/work-day-dto';
import {CreateEditClinicRequest} from '../../services/models/create-edit-clinic-request';
import {Router} from '@angular/router';
import {ObjectTransferService} from '../../services/my-services/object-transfer-service';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-add-clinic',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    UrlDirective,
    NgIf
  ],
  templateUrl: './add-edit-clinic.component.html',
  styleUrl: './add-edit-clinic.component.css'
})
export class AddEditClinicComponent implements OnInit {
  workdays: Map<String, WorkDayDto> = new Map();
  days: Set<String> = new Set();
  clinicId: string = '';
  clinicEdit: boolean = false;

  clinic: CreateEditClinicRequest = {
    address: '',
    city: '',
    description: '',
    identificationNumber: '',
    phoneNumber: '',
    pictureUrl: '',
    workingDays: [],
  };

  constructor(
    private clinicService: ClinicControllerService,
    private router: Router,
    private objectTransfer: ObjectTransferService
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

    if (this.objectTransfer.hasObject()) {
      this.clinic = this.objectTransfer.getObject();
      this.clinicId = this.objectTransfer.getObject().id
      this.clinicEdit = true;

      for (let day of this.clinic.workingDays) {
        this.workdays.set(day.dayName.toUpperCase(), day);
      }

      this.objectTransfer.clearObject();
    }
  }

  submit(form: NgForm) {
    if (form.invalid) {
      return;
    }

    const clinic: CreateEditClinicRequest = form.value;
    const clinicWorkdays: WorkDayDto[] = [];
    for (let day of this.workdays.values()) {
      clinicWorkdays.push(day);
    }
    clinic.workingDays = clinicWorkdays;

    if (this.clinicEdit) {
      this.clinicService.editClinic({'clinicId': this.clinicId.toString(), body: clinic}).subscribe({
        next: () => this.router.navigate(['/clinic', this.clinicId])
      })
    } else {
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

  showImage(form: NgForm) {
    this.clinic.pictureUrl = form.value.pictureUrl
  }
}
