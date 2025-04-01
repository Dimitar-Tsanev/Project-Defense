import {HttpInterceptorFn} from '@angular/common/http';
import {catchError} from 'rxjs';
import {inject} from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from '../services/my-services/auth.Service';

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

  return next(req).pipe(
    catchError((err) => {
      console.log(err);
      if (err.status === 401) {
        router.navigate(['/login']);
      }
      return [err];
    })
  );
};
