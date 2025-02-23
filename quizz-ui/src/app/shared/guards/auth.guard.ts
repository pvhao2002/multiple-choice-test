import {inject, Injectable} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
import {Observable} from 'rxjs';
import {AuthenticationService} from '../service/authentication.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  authService = inject(AuthenticationService);
  router = inject(Router);

  canActivate(): Observable<boolean> {
    return new Observable<boolean>(observer => {
      this.authService.getCurrentUser().subscribe(user => {
        if (user.valid) {
          observer.next(true);
        } else {
          observer.next(false);
          this.router.navigate(['/login']).then();
        }
        observer.complete();
      })
    })
  }
}
