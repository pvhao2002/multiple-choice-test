import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable, throwError} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {catchError, finalize, tap} from 'rxjs/operators';
import {LoginResponseDTO} from '../model/auth.model';
import {UserService} from '../service/user.service';
import {API_URL, CONSTANT} from '../shared/constant';
import {ResponseData} from '../model/response-data.model';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {
  private currentUser = new BehaviorSubject<LoginResponseDTO>(new LoginResponseDTO());
  private refreshing = false;
  public changePwdSuccess = false;
  public transactionCode = '';

  constructor(private http: HttpClient,
              private userService: UserService,) {
    if (localStorage.getItem(CONSTANT.authToken)) {
      const currentUser = JSON.parse(localStorage.getItem(CONSTANT.authToken) ?? '');
      this.currentUser.next(currentUser);
    }
  }

  get userInfo(): LoginResponseDTO {
    return this.currentUser.value;
  }

  public get isLoggedIn(): boolean {
    return this.currentUser.value.valid;
  }

  public logout() {
    this.http.post(API_URL.auth.logout, {})
      .pipe(
        finalize(() => {
          this.removeTokenStorage();
          this.userService.setProfile();
          window.location.assign(CONSTANT.studentPath);
        })
      )
      .subscribe();
  }

  getCurrentUser(): Observable<LoginResponseDTO> {
    return this.currentUser.asObservable();
  }

  getToken(): string {
    return this.currentUser.value.accessToken;
  }

  isRefreshing(): boolean {
    return this.refreshing;
  }

  doRefreshToken() {
    this.refreshing = true;
    const refreshToken = this.currentUser.value.refreshToken;
    return this.http.post<ResponseData<string[]>>(API_URL.auth.refreshToken, {rt: refreshToken}).pipe(
      tap((res) => {
        this.refreshing = false;
        if (res.success && res?.data) {
          const user = new LoginResponseDTO(res.data[0], refreshToken, false, false, true);
          this.setTokenStorage(user);
        } else {
          this.removeTokenStorage();
        }
      }),
      catchError((err) => {
        this.refreshing = false;
        this.logout();
        return throwError(() => new Error(err));
      }),
    );
  }

  removeTokenStorage(): void {
    this.currentUser.next(new LoginResponseDTO());
    localStorage.removeItem(CONSTANT.authToken);
    localStorage.removeItem('TAB');
    localStorage.removeItem('TAB_SELECTED');
  }

  setTokenStorage(user: LoginResponseDTO): void {
    if (user) {
      localStorage.setItem(CONSTANT.authToken, JSON.stringify(user));
      this.currentUser.next(user);
    } else {
      localStorage.removeItem(CONSTANT.authToken);
    }
  }

  login(loginId: string, password: string, captchaCode = '') {
    return this.http.post<ResponseData<LoginResponseDTO>>(API_URL.auth.login, {loginId, password, captchaCode})
      .pipe(
        tap((res) => {
          if (res.success) {
            const tokenObject = res.data;
            tokenObject.valid = true;
            this.setTokenStorage(tokenObject);
          } else {
            this.removeTokenStorage();
            window.location.assign(CONSTANT.studentPath);
          }
        }),
      );
  }

}
