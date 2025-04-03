import {Component, OnInit} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {LoaderComponent} from '../../common/loader/loader.component';
import {PatientInfo} from '../../services/models/patient-info';
import {AuthService} from '../../services/my-services/auth-service';
import {UserAccountControllerService} from '../../services/services/user-account-controller.service';

@Component({
  selector: 'app-profile',
  imports: [
    LoaderComponent,
    RouterLink
  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {

  user: PatientInfo | undefined = undefined;
  userAccountId: string | undefined = undefined;

  constructor(
    private authService: AuthService,
    private userService: UserAccountControllerService,
    private router: Router,
  ) {
  }

  ngOnInit() {
    if (this.authService.user?.patientInfo) {
      this.userAccountId = this.authService.user.accountId;
      this.user = this.authService.user?.patientInfo;

    } else {
      this.router.navigate(['login']);
    }
  }

  inactivateAccount() {
    if (this.userAccountId) {
      this.userService.deleteUserAccount({'accountId': this.userAccountId as string}).subscribe({
        next: () => {
          this.authService.logout()
          this.router.navigate(['clinics']);
        }
      })
    }
  }
}
