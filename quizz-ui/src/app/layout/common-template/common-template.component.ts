import {Component, computed, effect, inject, signal} from '@angular/core';
import {AuthenticationService} from '../../shared/service/authentication.service';
import {FooterComponent} from '../../component/footer/footer.component';
import {Router, RouterOutlet} from '@angular/router';
import {SideMenuComponent} from '../../component/side-menu/side-menu.component';
import {TopStartComponent} from '../../component/top-start/top-start.component';
import {CONSTANT} from '../../shared/constant';

@Component({
  selector: 'app-common-template',
  imports: [
    FooterComponent,
    RouterOutlet,
    SideMenuComponent,
    TopStartComponent
  ],
  templateUrl: './common-template.component.html',
  standalone: true,
  styleUrls: ['./common-template.component.scss', CONSTANT.lib1, CONSTANT.lib2]
})
export class CommonTemplateComponent {
  private router = inject(Router);

  currentUrl = signal(this.router.url);

  isStartTest = computed(() => {
    return this.currentUrl().includes('start');
  });

  constructor(protected authService: AuthenticationService) {
    effect(() => {
      this.router.events.subscribe(() => {
        this.currentUrl.set(this.router.url);
      });
    });
  }
}
