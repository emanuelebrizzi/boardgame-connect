import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'reservations',
    loadComponent: () =>
      import('./features/reservations/controller/reservation-list/reservation-list').then(
        (m) => m.ReservationListComponent
      ),
  },
  { path: '', redirectTo: 'reservations', pathMatch: 'full' },
];
