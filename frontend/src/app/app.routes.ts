import { Routes } from '@angular/router';
import { DashboardComponent } from './player-dashboard/dashboard.component';

export const routes: Routes = [
  {
    path: 'reservations',
    component: DashboardComponent,
  },
  { path: '', redirectTo: 'reservations', pathMatch: 'full' },
];
