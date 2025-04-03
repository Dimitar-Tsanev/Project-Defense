import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {PhysicianControllerService} from '../../services/services/physician-controller.service';
import {PhysicianInfo} from '../../services/models/physician-info';
import {AuthService} from '../../services/my-services/auth-service';
import {LoaderComponent} from '../../common/loader/loader.component';
import {PhysicianDaySchedulePublic} from '../../services/models/physician-day-schedule-public';
import {ScheduleControllerService} from '../../services/services/schedule-controller.service';

@Component({
  selector: 'app-physician',
  imports: [
    LoaderComponent,
    RouterLink
  ],
  templateUrl: './physician.component.html',
  styleUrl: './physician.component.css'
})
export class PhysicianComponent implements OnInit {
  physician: PhysicianInfo | undefined = undefined;
  schedules: Array<PhysicianDaySchedulePublic> = [];
  isLoading: boolean = true;
  isAuthenticated: boolean = false;
  accountId: string | undefined = undefined;

  constructor(
    private route: ActivatedRoute,
    private physicianService: PhysicianControllerService,
    private authService: AuthService,
    private scheduleService: ScheduleControllerService
  ) {
  }

  ngOnInit(): void {
    const id = this.route.snapshot.params['physicianId'];
    this.physicianService.getPhysicianInfo({'physicianId': id as string}).subscribe({
      next: (res) => {
        this.physician = res;
        this.isLoading = false;
        this.isAuthenticated = this.authService.isLogged()
        this.accountId = this.authService.user?.accountId
        this.getSchedules()
      }
    });
  }

  getSchedules() {
    if (this.physician) {
      this.scheduleService.getPublicPhysicianSchedules({'physicianId': this.physician.physicianId}).subscribe({
        next: (res) => {
          this.schedules = res;
        }
      })
    }
  }

  reserve(timeslotId: string) {
    if (this.accountId) {
      this.scheduleService.makeAppointment({
        'timeslotId': timeslotId as string,
        'accountId': this.accountId
      }).subscribe({
        next: () => {
          this.getSchedules();
          timeslotId = '';
        }
      })
    }
  }
}
