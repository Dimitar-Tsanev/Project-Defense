import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {UserDataResponse} from '../models/user-data-response';
import {AuthenticationControllerService} from '../services/authentication-controller.service';
import {LoginRequest} from '../models/login-request';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  user: UserDataResponse | null = null;
  tokenAccess: string = 'my_app_token';

  constructor(
    private authenticationService: AuthenticationControllerService,
    private router: Router
  ) {
  }

  setUser(user: LoginRequest): void {
    this.authenticationService.login$Response({body: user}).subscribe({
      next: (res) => {
        const token = this.parseToken(res.headers.get('authorization'));

        sessionStorage.setItem(this.tokenAccess, token);

        this.user = res.body;
        this.router.navigate(['/profile']);
      }
    })
  }

  getToken() {
    return sessionStorage.getItem(this.tokenAccess);
  }

  getUser(): UserDataResponse | null {
    return this.user
  }

  isLogged() {
    return this.user !== null;
  }

  isAdmin() {
    return this.user !== null && this.user.role === 'ADMIN';
  }

  hasCredentials() {
    return this.user !== null && this.user.role !== 'PATIENT';
  }

  logout() {
    sessionStorage.clear();
    this.user = null;
  }

  private parseToken(token: string | null) {
    if (token !== null) {
      return token.replace('"', '').split(' ')[1]
    }
    throw new Error('Unable to parse token');
  }
}
