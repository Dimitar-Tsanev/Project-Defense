import {Component} from '@angular/core';
import {EmailDirective} from "../../utils/validators/email.directive";
import {FormsModule, NgForm, ReactiveFormsModule} from "@angular/forms";
import {PasswordDirective} from "../../utils/validators/password.directive";
import {RouterLink} from "@angular/router";
import {LoginRequest} from '../../services/models/login-request';
import {AuthService} from '../../services/my-services/auth-service';

@Component({
  selector: 'app-login',
  imports: [
    EmailDirective,
    FormsModule,
    PasswordDirective,
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  loginRequest: LoginRequest = {email: '', password: ''};

  constructor(
    private auth: AuthService
  ) {
  }

  login(form: NgForm) {
    if (form.invalid) {
      return;
    }

    this.loginRequest.email = form.value.email;
    this.loginRequest.password = form.value.password;
    this.auth.setUser(this.loginRequest);
  }
}
