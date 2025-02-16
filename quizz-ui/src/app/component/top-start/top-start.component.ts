import {Component, computed, effect, signal} from '@angular/core';
import {CONSTANT} from '../../shared/constant';
import {UserService} from '../../shared/service/user.service';
import {interval} from 'rxjs';
import {DatePipe} from '@angular/common';
import {AuthenticationService} from '../../shared/service/authentication.service';

@Component({
  selector: 'app-top-start',
  imports: [
    DatePipe
  ],
  templateUrl: './top-start.component.html',
  standalone: true,
  styleUrls: ['./top-start.component.scss', CONSTANT.lib1, CONSTANT.lib2]
})
export class TopStartComponent {
  date = signal(new Date());

  constructor(protected userService: UserService,
              private authService: AuthenticationService
  ) {
    effect(() => {
      interval(1000).subscribe(() => {
        this.date.set(new Date());
      });
    });
    this.userService.getProfileData().subscribe();
  }

  logout() {
    this.authService.logout();
  }
}
