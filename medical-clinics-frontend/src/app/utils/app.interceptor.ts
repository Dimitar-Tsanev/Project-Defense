import {HttpInterceptorFn} from '@angular/common/http';
import {catchError} from 'rxjs';
import {inject} from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from '../services/my-services/auth-service';
import {ErrorMessagesService} from '../services/my-services/error-messages-service';

export const appInterceptor: HttpInterceptorFn = (req, next) => {
  if (!req.url.includes("/auth") &&
    !(req.url.includes("/clinics/") && req.method.toLowerCase() === "get") &&
    !(req.url.includes("/physicians/") && req.method.toLowerCase() === "get")) {

    const authService = inject(AuthService);
    const token = authService.getToken();

    req = req.clone({
      headers: req.headers.set('Authorization',`Bearer ${token}` )
    });
    console.log(req);
  }

  const router = inject(Router);
  const errorService = inject(ErrorMessagesService)

  return next(req).pipe(
    catchError((err) => {
      if (err.status === 401) {
        router.navigate(['/login']);
      }
      if(err.status === 403) {
        router.navigate(['/forbidden']);
      }
      if(err.status === 500) {
        router.navigate(['/internal-server-error']);
      }
      if(err.status === 404 || err.status === 400 || err.status === 409) {
        errorService.solve(err);
        router.navigate(['/errors']);
      }
      return [err];
    })
  );
};
