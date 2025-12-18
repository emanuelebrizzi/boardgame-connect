import { Routes } from '@angular/router';
import { authGuard } from './auth/auth-guard';
import { guestGuard } from './auth/guest-guard';

export const routes: Routes = [
  {
    path: '',
    canActivate: [guestGuard],
    loadComponent: () => import('./home/home').then((m) => m.Home),
    pathMatch: 'full',
  },
  {
    path: 'login',
    loadComponent: () => import('./auth/login/login').then((m) => m.Login),
  },
  {
    path: 'reservations',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./player-dashboard/dashboard.component').then((m) => m.DashboardComponent),
  },
  {
    path: 'reservations/:id',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./player-dashboard/show-reservations/reservation-detail/reservation-detail').then(
        (m) => m.ReservationDetail
      ),
  },
  {
    path: 'error',
    loadComponent: () => import('./error/error').then((m) => m.Error),
    data: {
      code: '404',
      title: 'Page Not Found',
      message: 'The page you are looking for does not exist.',
    },
  },
  {
    path: '**',
    loadComponent: () => import('./error/error').then((m) => m.Error),
    data: {
      code: '404',
      title: 'Page Not Found',
      message: 'The page you are looking for does not exist.',
    },
  },
];
