import {Routes} from '@angular/router';
import {AuthGuard} from './shared/guards/auth.guard';
import {CommonTemplateComponent} from './layout/common-template/common-template.component';
import {profileResolver} from './shared/service/user.service';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./component/login/login.component')
      .then((mod) => mod.LoginComponent),
  },
  {
    path: 'register',
    loadComponent: () => import('./component/register/register.component')
      .then((mod) => mod.RegisterComponent),
  },
  {
    path: 'student',
    component: CommonTemplateComponent,
    canActivate: [AuthGuard],
    resolve: {
      profile: profileResolver
    },
    children: [
      {
        path: '',
        redirectTo: 'home',
        pathMatch: 'full'
      },
      {
        path: 'home',
        loadComponent: () => import('./component/student/home/home.component')
          .then((mod) => mod.HomeComponent),
      }
    ]
  },
  {
    path: 'admin',
    component: CommonTemplateComponent,
    canActivate: [AuthGuard],
    resolve: {
      // profile: profileResolver
    },
    children: [
      {
        path: '',
        redirectTo: 'home',
        pathMatch: 'full'
      },
      {
        path: 'home',
        loadComponent: () => import('../app/component/admin/home-admin/home-admin.component')
          .then((mod) => mod.HomeAdminComponent),
      },
      {
        path: 'exam',
        children: [
          {
            path: 'list',
            loadComponent: () => import('../app/component/admin/exam-list/exam-list.component')
              .then((mod) => mod.ExamListComponent),
          },
          {
            path: 'upsert',
            loadComponent: () => import('../app/component/admin/exam-upsert/exam-upsert.component')
              .then((mod) => mod.ExamUpsertComponent),
          },
          {
            path: '',
            redirectTo: 'list',
            pathMatch: 'full'
          }
        ]
      },
      {
        path: 'subject',
        children: [
          {
            path: 'list',
            loadComponent: () => import('../app/component/admin/subject-list/subject-list.component')
              .then((mod) => mod.SubjectListComponent),
          },
          {
            path: 'upsert',
            loadComponent: () => import('../app/component/admin/subject-upsert/subject-upsert.component')
              .then((mod) => mod.SubjectUpsertComponent),
          },
          {
            path: '',
            redirectTo: 'list',
            pathMatch: 'full'
          }
        ]
      },
      {
        path: 'user-management',
        loadComponent: () => import('../app/component/admin/user-management/user-management.component')
          .then((mod) => mod.UserManagementComponent),
      }
    ]
  },
  {
    path: '**',
    redirectTo: 'login',
    pathMatch: 'full'
  }
];
