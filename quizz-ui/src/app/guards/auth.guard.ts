import {inject, Injectable} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
import {Observable} from 'rxjs';
import {AuthenticationService} from '../service/AuthenticationService';
import {CONSTANT} from '../shared/constant';

@Injectable({
  providedIn: 'root'
})

export class AuthGuard implements CanActivate {
  authService = inject(AuthenticationService);
  router = inject(Router);
  urlNotToRedirect = [CONSTANT.login, CONSTANT.register];

  canActivate(): Observable<boolean> {
    return new Observable<boolean>(observer => {
      this.authService.getCurrentUser().subscribe(user => {
        if (user.valid) {
          observer.next(true);
          window.location.assign(CONSTANT.studentPath);
        } else {
          observer.next(false);
        }
        observer.complete();
      })
    })
  }
}
