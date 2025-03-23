import {CustomPagingModel} from './model/custom-paging.model';

export const CONSTANT = {
  configs: {
    timeZone: '-04:00',
  },
  authToken: 'authToken',
  supportedLanguages: ['en_US', 'ko_KR'],
  defaultLocale: 'en_US',
  defaultFormatDatetime: 'yyyy-MM-dd hh:mm:ss',
  defaultFormatDate: 'yyyy-MM-dd',
  studentPath: '/student',
  adminPath: '/admin',
  loginPath: '/login',
  login: 'login',
  register: 'register',
  BE_URL_LOCAL: 'http://localhost:1122',
  bearer: 'Bearer ',
  lib1: '../../shared/styles/app.min.css',
  lib2: '../../shared/styles/icons.min.css',
  defaultNumberQuestion: 1,
};

export const CHART_OPTIONS = {
  init: {
    labels: [],
    datasets: [{data: [], label: ''}]
  },
  bar: {
    scales: {
      x: {},
      y: {
        min: 0,
      },
    },
    plugins: {
      legend: {
        display: true,
      },
      datalabels: {
        anchor: 'end',
        align: 'end',
      },
      subtitle: {
        display: true,
        text: 'Statistic user test by exam'
      }
    },
  }
};

export const DEFAULT_PAGING_CONFIG: CustomPagingModel = {
  pageNo: 1,
  pageSize: 10,
  totalRows: 0,
  totalPages: 0,
};

export const TimeZone = 'GMT+07:00'

export const PAGE_SIZE_OPTIONS = [5, 10, 15, 25, 100];

export const INFINITE_SCROLL_CONFIG = {
  distance: 2,
  throttle: 300,
};

export const API_URL = {
  authPrefix: '/auth/',
  translatePath: './assets/i18n/',
  auth: {
    info: '/api/users/info',
    login: '/api/auth/login',
    register: '/api/auth/register',
    logout: '/api/auth/logout',
    refreshToken: '/api/auth/refresh-token',
    captcha: '/api/auth/captcha?t={0}',
  }
};

