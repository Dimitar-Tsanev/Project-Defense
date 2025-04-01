import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/my-services/auth.Service';
import {LoaderComponent} from '../loader/loader.component';


@Component({
  selector: 'app-authentication',
  imports: [
    LoaderComponent
  ],
  templateUrl: './authentication.component.html',
  styleUrl: './authentication.component.css'
})
export class AuthenticationComponent implements OnInit {
  isAuthenticating = true;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    if(this.authService.isLogged()){
      this.isAuthenticating = false;
    }
  }
}
