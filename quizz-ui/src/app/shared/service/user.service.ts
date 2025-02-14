import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ResolveFn} from '@angular/router';
import {Observable} from 'rxjs';
import {ProfileDTO, UserInfo} from '../model/profile.model';
import {ResponseData} from '../model/response-data.model';
import {API_URL, CONSTANT} from '../constant';

@Injectable({providedIn: 'root'})
export class UserService {
  private userProfile = new UserInfo();

  constructor(private httpClient: HttpClient,) {
  }

  get profile(): UserInfo {
    return this.userProfile;
  }

  setProfile(profile = new UserInfo()): void {
    this.userProfile = profile;
  }

  getProfileData(): Observable<UserInfo> {
    return new Observable<UserInfo>((subscriber) => {
      const sub = this.httpClient.get<ResponseData<UserInfo>>(API_URL.auth.info).subscribe({
        next: (res) => {
          if (res.success) {
            this.setProfile(res.data);
          }
          subscriber.next(res.data);
        },
        error: (err) => {
          if (err?.error?.code === 401) {
            localStorage.removeItem(CONSTANT.authToken);
            window.location.href = '/#/' + CONSTANT.login;
          }
          subscriber.error(err.error);
        },
        complete: () => subscriber.complete()
      });
      return () => sub.unsubscribe();
    });
  }
}

export const profileResolver: ResolveFn<Observable<UserInfo>> = () => {
  return inject(UserService).getProfileData();
};
