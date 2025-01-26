import {CustomPagingModel} from '../model/custom-paging.model';

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
};

export const DEFAULT_PAGING_CONFIG: CustomPagingModel = {
  pageNo: 1,
  pageSize: 25,
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
    info: '/api/auth/info',
    login: '/api/auth/login',
    register: '/api/auth/register',
    logout: '/api/auth/logout',
    refreshToken: '/api/auth/refresh-token',
    captcha: '/api/auth/captcha?t={0}',
  }
};

