import {Component, OnInit} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {AuthenticationService} from './shared/service/authentication.service';
import {SideMenuComponent} from './component/side-menu/side-menu.component';
import {TopStartComponent} from './component/top-start/top-start.component';
import {FooterComponent} from './component/footer/footer.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, SideMenuComponent, TopStartComponent, FooterComponent],
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
