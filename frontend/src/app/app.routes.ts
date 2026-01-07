import { Routes } from '@angular/router';
import { authGuard } from './guards/auth-guard';
import { guestGuard } from './guards/guest-guard';
import { associationGuard, playerGuard } from './guards/role-guard';
import { dashboardDispatcherGuard } from './guards/dashboard-guard';

export const routes: Routes = [
  {
    path: '',
    canActivate: [guestGuard],
    loadComponent: () => import('./components/home/home').then((m) => m.Home),
    pathMatch: 'full',
  },
  {
    path: 'login',
    canActivate: [guestGuard],
    loadComponent: () => import('./components/login/login').then((m) => m.Login),
  },
  {
    path: 'register',
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./components/registration/registration').then((m) => m.Registration),
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    children: [
      {
        path: 'player',
        loadComponent: () =>
          import('./components/dashboard/player-dashboard/player-dashboard').then(
            (m) => m.PlayerDashboard
          ),
        canActivate: [playerGuard],
      },
      {
        path: 'association',
        loadComponent: () =>
          import('./components/dashboard/association-dashboard/association-dashboard').then(
            (m) => m.AssociationDashboard
          ),
        canActivate: [associationGuard],
      },
      {
        path: '',
        pathMatch: 'full',
        canActivate: [dashboardDispatcherGuard],
        loadComponent: () =>
          import('./components/dashboard/player-dashboard/player-dashboard').then(
            (m) => m.PlayerDashboard
          ),
      },
    ],
  },
  {
    path: 'reservations/:id',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./components/dashboard/show-reservations/reservation-detail/reservation-detail').then(
        (m) => m.ReservationDetail
      ),
  },
  {
    path: 'boardgames/create',
    loadComponent: () =>
      import('./components/boardgame/create-boardgame/create-boardgame').then(
        (m) => m.CreateBoardgame
      ),
    canActivate: [associationGuard],
  },
  {
    path: 'error',
    loadComponent: () => import('./components/error/error').then((m) => m.Error),
    data: {
      code: '404',
      title: 'Page Not Found',
      message: 'The page you are looking for does not exist.',
    },
  },
  {
    path: '**',
    loadComponent: () => import('./components/error/error').then((m) => m.Error),
    data: {
      code: '404',
      title: 'Page Not Found',
      message: 'The page you are looking for does not exist.',
    },
  },
];
