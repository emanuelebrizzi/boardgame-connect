import { Routes } from '@angular/router';
import { authGuard } from './auth/auth-guard';

export const routes: Routes = [
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
    path: '',
    redirectTo: 'reservations',
    pathMatch: 'full',
  },
  {
    // TODO: Error page for 404
    path: '**',
    redirectTo: 'reservations',
  },
];
