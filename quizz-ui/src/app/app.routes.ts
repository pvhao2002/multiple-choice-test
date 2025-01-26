import {Routes} from '@angular/router';
import {AuthGuard} from './guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('../app/component/login/login.component')
      .then((mod) => mod.LoginComponent),
  },
  {
    path: 'register',
    loadComponent: () => import('../app/component/register/register.component')
      .then((mod) => mod.RegisterComponent),
  },
  {
    path: '',
    loadComponent: () => import('../app/component/dashboard/dashboard.component')
      .then((mod) => mod.DashboardComponent),
    canActivate: [AuthGuard],
    children: [
      {
        path: 'home',
        loadComponent: () => import('../app/component/home/home.component')
          .then((mod) => mod.HomeComponent),
      }
    ]
  }
];
