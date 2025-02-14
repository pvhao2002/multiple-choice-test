import {Injectable} from '@angular/core';
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {filter, Observable, switchMap, take, throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {API_URL} from '../constant';
import {ResponseData} from '../model/response-data.model';
import {LoginResponseDTO} from '../model/auth.model';
import {AuthenticationService} from '../service/authentication.service';

@Injectable({
  providedIn: 'root',
})
export class ErrorInterceptor implements HttpInterceptor {
  constructor(private authService: AuthenticationService) {
  }

  intercept(req: HttpRequest<object>, next: HttpHandler): Observable<HttpEvent<object>> {
    let authReq = req;
    if (this.authService.isLoggedIn) {
      authReq = this.addTokenHeader(req, this.authService.getToken());
    }

    return next.handle(authReq).pipe(catchError(error => {
      if (error instanceof HttpErrorResponse && !authReq.url.includes(API_URL.authPrefix) && error.status === 401) {
        return this.handle401Error(authReq, next);
      }
      return throwError(error);
    }));
  }

  private handle401Error(request: HttpRequest<object>, next: HttpHandler): Observable<HttpEvent<object>> {
    if (!this.authService.isRefreshing()) {
      return this.authService.doRefreshToken().pipe(
        switchMap((res: ResponseData<string[]>) => next.handle(this.addTokenHeader(request, res.data[0]))),
        catchError((err) => {
          this.authService.logout();
          return throwError(err);
        }),
      );
    }

    return this.authService.getCurrentUser().pipe(
      filter((token) => token.valid),
      take(1),
      switchMap((token: LoginResponseDTO) => next.handle(this.addTokenHeader(request, token.accessToken))),
    );
  }

  private addTokenHeader(request: HttpRequest<object>, token: string) {
    if (!token || request.url.indexOf('/auth/authentication') > -1) {
      return request;
    }
    return request.clone({headers: request.headers.set('authorization', token)});
  }
}
