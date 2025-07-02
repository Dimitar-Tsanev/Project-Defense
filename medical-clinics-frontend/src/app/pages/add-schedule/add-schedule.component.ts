import {Component, OnInit} from '@angular/core';
import {NewDaySchedule} from '../../services/models/new-day-schedule';
import {FormsModule, NgForm, ReactiveFormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {ScheduleControllerService} from '../../services/services/schedule-controller.service';
import {AuthService} from '../../services/my-services/auth-service';

@Component({
  selector: 'app-add-schedule',
  imports: [
    FormsModule,
    ReactiveFormsModule,
  ],
  templateUrl: './add-schedule.component.html',
  styleUrl: './add-schedule.component.css'
})
export class AddScheduleComponent implements OnInit {
  schedules: Array<NewDaySchedule> = [];

  userId: string | undefined = undefined;

  constructor(
    private router: Router,
    private scheduleService: ScheduleControllerService,
    private authService: AuthService
  ) {
  }

  ngOnInit() {
    const user = this.authService.getUser();
    if (user) {
      this.userId = user.accountId
    }
  }

  pushSchedule(newSchedule: NgForm) {
    if (newSchedule.invalid) {
      return;
    }
    let schedule: NewDaySchedule = {
      date: newSchedule.value.date,
      startTime: newSchedule.value.startTime + ':00',
      endTime: newSchedule.value.endTime + ':00',
      timeSlotInterval: newSchedule.value.timeSlotInterval
    }
    this.schedules.push(schedule);
  }

  submitSchedule() {
    if (this.schedules.length !== 0 && this.userId) {
      this.scheduleService.generateSchedule({'accountId': this.userId, body: this.schedules}).subscribe({
          next: () => {
            this.schedules = [];
            this.router.navigate(['clinics']);
          }
        })
    }
  }

  removeSchedule(index: number) {
    this.schedules.splice(index, 1);
  }
}
