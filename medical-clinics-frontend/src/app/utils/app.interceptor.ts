import {HttpInterceptorFn, HttpResponse} from '@angular/common/http';
import {catchError} from 'rxjs';
import {inject} from '@angular/core';
import {Router} from '@angular/router';
import {AuthenticationControllerService} from '../services/services/authentication-controller.service';
import {map} from 'rxjs/operators';

const API = '/api';

export const appInterceptor: HttpInterceptorFn = (req, next) => {
  if (!req.url.includes("/auth") ||
    (req.url.includes("/clinics/") && req.method.toLowerCase() === "get") ||
    (req.url.includes("/physicians/") && req.method.toLowerCase() === "get")) {

    req = req.clone({
      withCredentials: true,
    });
  }
  const router = inject(Router);

  return next(req).pipe(
    catchError((err) => {
      if (err.status === 401) {
        router.navigate(['/login']);
      }
      return [err];
    })
  );
};
