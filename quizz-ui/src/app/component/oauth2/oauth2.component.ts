import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {AuthenticationService} from '../../shared/service/authentication.service';
import {LoginResponseDTO} from '../../shared/model/auth.model';

@Component({
  selector: 'app-oauth2',
  imports: [],
  templateUrl: './oauth2.component.html',
  standalone: true,
  styleUrl: './oauth2.component.scss'
})
export class Oauth2Component implements OnInit {
  constructor(private route: ActivatedRoute,
              private authenService: AuthenticationService) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const token = params['token'];
      const loginDto = new LoginResponseDTO(token, '', false, true);
      this.authenService.setTokenStorage(loginDto);
      this.authenService.redirectHome();
    });
  }

}
