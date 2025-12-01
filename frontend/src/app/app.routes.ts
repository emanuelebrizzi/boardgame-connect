import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'reservations',
    loadComponent: () =>
      import('./player-dashboard/dashboard.component').then((m) => m.DashboardComponent),
  },
  {
    path: 'reservations/:id',
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
