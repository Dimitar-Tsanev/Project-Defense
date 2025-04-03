import {Component, input} from '@angular/core';
import {FormsModule, NgForm} from '@angular/forms';
import {AuthenticationControllerService} from '../../services/services/authentication-controller.service';
import {Router, RouterLink} from '@angular/router';
import {RegisterRequest} from '../../services/models/register-request';
import {PasswordDirective} from '../../utils/validators/password.directive';
import {EmailDirective} from '../../utils/validators/email.directive';


@Component({
  selector: 'app-register',
  imports: [
    FormsModule, RouterLink, PasswordDirective, EmailDirective,
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  request: RegisterRequest = {firstName: '', lastName: '', email: '', password: '', phone:null};

  constructor(
    private authenticationService: AuthenticationControllerService,
    private router: Router,
  ) {
  }

  register(form: NgForm) {
    if (form.invalid) {
      return;
    }
    this.request.firstName = form.value.firstName;
    this.request.lastName = form.value.lastName;
    this.request.email = form.value.email;
    this.request.password = form.value.password;
    this.request.phone = form.value.phone.length > 0? form.value.phone : null;


    this.authenticationService.register({body: this.request}).subscribe({
      next: () => this.router.navigate(['/login'])
    });
  }

  protected readonly input = input;
}
