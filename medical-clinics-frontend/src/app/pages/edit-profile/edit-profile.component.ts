import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {UserAccountControllerService} from '../../services/services/user-account-controller.service';
import {FormsModule, NgForm, ReactiveFormsModule} from '@angular/forms';
import {UserAccountEditRequest} from '../../services/models/user-account-edit-request';
import {AuthService} from '../../services/my-services/auth-service';
import {EmailDirective} from '../../utils/validators/email.directive';
import {PasswordDirective} from '../../utils/validators/password.directive';
import {PatientInfo} from '../../services/models/patient-info';

@Component({
  selector: 'app-edit-profile',
  imports: [
    EmailDirective,
    FormsModule,
    PasswordDirective,
    ReactiveFormsModule,
  ],
  templateUrl: './edit-profile.component.html',
  styleUrl: './edit-profile.component.css'
})
export class EditProfileComponent implements OnInit {
  accountId: string = '';
  userData: PatientInfo = {
    address: 'dontwork', city: '', country: '', email: '', firstName: '',
    identificationCode: '', lastName: '', patientId: '', phone: ''
  }

  constructor(
    private route: ActivatedRoute,
    private userService: UserAccountControllerService,
    private authService: AuthService,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.accountId = this.route.snapshot.params['userAccountId'];
    const user = this.authService.getUser()
    if (user?.patientInfo) {
      this.userData = user.patientInfo;
    }
  }

  editProfile(form: NgForm) {
    if (form.invalid) {
      return;
    }

    const profile: UserAccountEditRequest = form.value;

    profile.id = this.accountId;
    profile.email = form.value.email;
    profile.firstName = form.value.firstName;
    profile.lastName = form.value.lastName;

    if (0 === profile.address?.length) {
      profile.address = null;
    }
    if (0 === profile.city?.length) {
      profile.city = null;
    }
    if (0 === profile.country?.length) {
      profile.country = null;
    }
    if (0 === profile.newPassword?.length) {
      profile.newPassword= null;
    }
    if (0 === profile.oldPassword?.length) {
      profile.oldPassword = null;
    }
    if (0 === profile.phone?.length) {
      profile.phone = null;
    }

    this.userService.updateUserAccount({
      'accountId': this.accountId as string,
      body: profile
    }).subscribe({
      next: () => {
        this.authService.logout()
        this.router.navigate(['/login']);
      }
    });
  }
}
