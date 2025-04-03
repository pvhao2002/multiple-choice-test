import {Routes} from '@angular/router';
import {AuthGuard} from './shared/guards/auth.guard';
import {CommonTemplateComponent} from './layout/common-template/common-template.component';
import {profileResolver} from './shared/service/user.service';

export const routes: Routes = [
  {
    path: 'oauth2/redirect',
    loadComponent: () => import('./component/oauth2/oauth2.component')
      .then((mod) => mod.Oauth2Component),
  },
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
        redirectTo: 'about',
        pathMatch: 'full'
      },
      {
        path: 'home',
        loadComponent: () => import('./component/student/home/home.component')
          .then((mod) => mod.HomeComponent),
      },
      {
        path: 'exam',
        loadComponent: () => import('./component/student/exam-list/exam-list.component')
          .then((mod) => mod.ExamListComponent),
      },
      {
        path: 'subject',
        loadComponent: () => import('./component/student/subject-list/subject-list.component')
          .then((mod) => mod.SubjectListComponent),
      },
      {
        path: 'start',
        loadComponent: () => import('./component/student/start-test/start-test.component')
          .then((mod) => mod.StartTestComponent),
      },
      {
        path: 'result',
        loadComponent: () => import('./component/student/learning-result/learning-result.component')
          .then((mod) => mod.LearningResultComponent),
      },
      {
        path: 'chat-bot',
        loadComponent: () => import('./component/student/chat-bot/chat-bot.component')
          .then((mod) => mod.ChatBotComponent),
      },
      {
        path: 'course',
        loadComponent: () => import('./component/student/course-list/course-list.component')
          .then((mod) => mod.CourseListComponent),
      },
      {
        path: 'course-detail',
        loadComponent: () => import('./component/student/course-list/course-detail/course-detail.component')
          .then((mod) => mod.CourseDetailComponent),
      },
      {
        path: 'about',
        loadComponent: () => import('./component/student/about/about.component')
          .then((mod) => mod.AboutComponent),
      }
    ]
  },
  {
    path: 'admin',
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
        path: 'about',
        loadComponent: () => import('../app/component/admin/about-manage/about-manage.component')
          .then((mod) => mod.AboutManageComponent),
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
            path: 'detail',
            loadComponent: () => import('../app/component/admin/exam-detail/exam-detail.component')
              .then((mod) => mod.ExamDetailComponent),
          },
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
        path: 'course',
        children: [
          {
            path: 'list',
            loadComponent: () => import('../app/component/admin/course-list/course-list.component')
              .then((mod) => mod.CourseListComponent),
          },
          {
            path: 'upsert',
            loadComponent: () => import('../app/component/admin/course-upsert/course-upsert.component')
              .then((mod) => mod.CourseUpsertComponent),
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
