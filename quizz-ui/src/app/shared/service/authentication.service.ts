import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable, throwError} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {catchError, tap} from 'rxjs/operators';
import {LoginDTO, LoginResponseDTO} from '../model/auth.model';
import {UserService} from './user.service';
import {API_URL, CONSTANT} from '../constant';
import {ResponseData} from '../model/response-data.model';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {
  private currentUser = new BehaviorSubject<LoginResponseDTO>(new LoginResponseDTO());
  private refreshing = false;

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

  public get isAdmin(): boolean {
    return this.currentUser.value.isAdmin;
  }

  public logout() {
    this.removeTokenStorage();
    this.userService.setProfile();
    this.redirectLogin();
  }

  getCurrentUser(): Observable<LoginResponseDTO> {
    return this.currentUser.asObservable();
  }

  getToken(): string {
    return `Bearer ${this.currentUser.value.accessToken}`;
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
          const user = new LoginResponseDTO(res.data[0], refreshToken, false, true);
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
  }

  setTokenStorage(user: LoginResponseDTO): void {
    if (user) {
      localStorage.setItem(CONSTANT.authToken, JSON.stringify(user));
      this.currentUser.next(user);
    } else {
      localStorage.removeItem(CONSTANT.authToken);
    }
  }

  login(email: string, password: string, captchaCode = '') {
    return this.http.post<ResponseData<LoginResponseDTO>>(API_URL.auth.login, {email, password, captcha: captchaCode})
      .pipe(
        tap((res) => {
          if (res.success) {
            const tokenObject = res.data;
            this.setTokenStorage(tokenObject);
          } else {
            this.removeTokenStorage();
            this.redirectLogin();
          }
        }),
      );
  }

  loginV2(payload: LoginDTO) {
    return this.login(payload.email, payload.password, payload.captcha);
  }

  redirectLogin() {
    window.location.href = `/#/${CONSTANT.login}`;
  }

  redirectHome() {
    window.location.href = this.currentUser.value.isAdmin ? `/#${CONSTANT.adminPath}` : `/#${CONSTANT.studentPath}`;
  }

}
