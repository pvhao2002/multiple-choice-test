import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ResolveFn} from '@angular/router';
import {Observable} from 'rxjs';
import {ProfileDTO} from '../model/profile.model';
import {ResponseData} from '../model/response-data.model';
import {API_URL, CONSTANT} from '../shared/constant';

@Injectable({providedIn: 'root'})
export class UserService {
  private userProfile = new ProfileDTO();

  constructor(private httpClient: HttpClient,) {
  }

  get profile(): ProfileDTO {
    return this.userProfile;
  }

  setProfile(profile = new ProfileDTO()): void {
    this.userProfile = profile;
  }

  getProfileData(): Observable<ProfileDTO> {
    return new Observable<ProfileDTO>((subscriber) => {
      const sub = this.httpClient.get<ResponseData<ProfileDTO>>(API_URL.auth.info).subscribe({
        next: (res) => {
          if (res.success) {
            this.setProfile(res.data);
          }
          subscriber.next(res.data);
        },
        error: (err) => {
          subscriber.error(err.error);
          if (err?.error?.code === 401) {
            localStorage.removeItem(CONSTANT.authToken);
            localStorage.removeItem('TAB');
            localStorage.removeItem('TAB_SELECTED');
            window.location.assign(CONSTANT.studentPath);
          }
        },
        complete: () => subscriber.complete()
      });
      return () => sub.unsubscribe();
    });
  }
}

export const profileResolver: ResolveFn<Observable<ProfileDTO>> = () => {
  return inject(UserService).getProfileData();
};
