import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth/auth-service';

export const associationGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const user = authService.currentUser();

  if (user && user.role === 'ASSOCIATION') {
    return true;
  }

  return router.createUrlTree(['/dashboard/player']);
};

export const playerGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const user = authService.currentUser();

  if (user && user.role === 'PLAYER') {
    return true;
  }

  return router.createUrlTree(['/dashboard/association']);
};
