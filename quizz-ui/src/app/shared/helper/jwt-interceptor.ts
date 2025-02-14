import {Injectable} from '@angular/core';
import {HttpRequest, HttpHandler, HttpEvent, HttpInterceptor} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AuthenticationService} from '../service/authentication.service';

@Injectable({
  providedIn: 'root',
})
export class JwtInterceptor implements HttpInterceptor {
  token!: string;

  constructor(private authService: AuthenticationService) {
    this.authService.getCurrentUser().subscribe({
      next: user => (this.token = user.accessToken)
    });
  }

  intercept(request: HttpRequest<object>, next: HttpHandler): Observable<HttpEvent<object>> {
    if (this.token) {
      request = request.clone({
        setHeaders: {
          authorization: `Bearer ${this.token}`
        }
      });
    }

    return next.handle(request);
  }
}
