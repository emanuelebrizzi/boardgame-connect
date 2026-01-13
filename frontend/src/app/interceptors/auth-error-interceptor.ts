import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth-service';
import { catchError, throwError } from 'rxjs';

export const authErrorInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return next(req).pipe(
    catchError((err: unknown) => {
      if (err instanceof HttpErrorResponse) {
        // 401 Unauthorized: Token expired or invalid -> Log out and redirect
        if (err.status === 401) {
          authService.logout();
          router.navigate(['/login']);
        }

        // 403 Forbidden: User has no permission -> Redirect to Error page
        if (err.status === 403) {
          router.navigate(['/error'], {
            queryParams: {
              code: '403',
              title: 'Access Denied',
              message: 'You do not have permission to view this area.',
            },
          });
        }
      }
      return throwError(() => err);
    })
  );
};
