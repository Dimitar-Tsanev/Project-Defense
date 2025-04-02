import {inject} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot,} from '@angular/router';
import {AuthService} from '../services/my-services/auth-service';


export const CredentialsGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.hasCredentials()) {
    return true;
  }

  router.navigate(['/clinics']);
  return false;
};
