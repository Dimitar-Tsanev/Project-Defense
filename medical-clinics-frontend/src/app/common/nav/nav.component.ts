import {Component} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {AuthService} from '../../services/my-services/auth-service';

@Component({
  selector: 'app-nav',
  imports: [
    RouterLink
  ],
  templateUrl: './nav.component.html',
  styleUrl: './nav.component.css'
})
export class NavComponent {

  constructor(private authService: AuthService, private router: Router) {
  }

  isLogged(): boolean {
    return this.authService.isLogged();
  }

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }


  logout() {
    this.authService.logout();
    this.router.navigate(['/clinics']);
  }

  hasCredentials() {
    return this.authService.hasCredentials();
  }
}
