import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth/auth-service';

export const dashboardDispatcherGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const user = authService.currentUser();

  if (user?.role === 'ASSOCIATION') {
    return router.createUrlTree(['/dashboard/association']);
  } else if (user?.role === 'PLAYER') {
    return router.createUrlTree(['/dashboard/player']);
  }

  return router.createUrlTree(['/login']);
};
