import { Routes } from '@angular/router';
import { authGuard } from './guards/auth-guard';
import { guestGuard } from './guards/guest-guard';
import { associationGuard } from './guards/role-guard';
import { CreateBoardgame } from './components/boardgame/create-boardgame/create-boardgame';

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
    loadComponent: () => import('./components/auth/login/login').then((m) => m.Login),
  },
  {
    path: 'register',
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./components/auth/registration/registration').then((m) => m.Registration),
  },
  {
    path: 'reservations',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./components/dashboard/dashboard').then((m) => m.DashboardComponent),
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
    component: CreateBoardgame,
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
