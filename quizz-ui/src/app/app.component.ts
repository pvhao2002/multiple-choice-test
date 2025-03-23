import {Component, OnInit} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {AuthenticationService} from './shared/service/authentication.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  standalone: true,
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title = 'quizz-ui';

  constructor(protected authService: AuthenticationService) {
  }

  ngOnInit() {

  }

}
