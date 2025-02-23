import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {NgOptimizedImage} from '@angular/common';
import {ForgetPassword} from '../../shared/model/forget-password';
import {FormsModule} from '@angular/forms';
import {BsModalService} from 'ngx-bootstrap/modal';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {faEnvelope} from '@fortawesome/free-solid-svg-icons';
import {LoginDTO} from '../../shared/model/auth.model';
import {HttpClient} from '@angular/common/http';
import {ResponseData} from '../../shared/model/response-data.model';
import {AuthenticationService} from '../../shared/service/authentication.service';
import {ToastrService} from 'ngx-toastr';
import {Router} from '@angular/router';
import {CONSTANT} from '../../shared/constant';

@Component({
  selector: 'app-login',
  imports: [
    NgOptimizedImage,
    FormsModule,
    FontAwesomeModule
  ],
  templateUrl: './login.component.html',
  standalone: true,
  styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit {
  @ViewChild('forgetPassword') forgetPasswordTemplate!: TemplateRef<any>;
  paramForgetPassword: ForgetPassword = new ForgetPassword();
  faEnvelope = faEnvelope;
  loginPayload: LoginDTO = new LoginDTO();
  captchaUrl = '/api/auth/captcha';

  constructor(
    private bsModalService: BsModalService
    , private http: HttpClient
    , private authService: AuthenticationService
    , private toastr: ToastrService
    , private router: Router
  ) {
  }

  handleClose() {
    this.bsModalService.hide();
  }

  resetPassword() {

  }

  openForgetPassword() {
    this.bsModalService.show(this.forgetPasswordTemplate, {
      class: 'modal-lg modal-dialog-centered',
    });
  }

  getCaptcha() {
    this.captchaUrl = `/api/auth/captcha?${Date.now()}`;
  }

  ngOnInit(): void {
    if (this.authService.isLoggedIn) {
      this.authService.redirectHome();
    }
  }

  doLogin() {
    this.authService.loginV2(this.loginPayload).subscribe({
      next: (res) => {
        if (!res.success) {
          this.toastr.error(res.message);
          if (res.errorCode === 'ACCOUNT_INACTIVE') {

          }
        } else {
          this.toastr.success('Login success');
          this.authService.redirectHome();
        }
      }
    });
  }

  navigateToRegister() {
    this.router.navigate(['/register']).then();
  }

  loginWithSocial(provider: string) {
    switch (provider) {
      case 'google':
        window.location.href = `${CONSTANT.BE_URL_LOCAL}/api/oauth2/authorize/google?redirect_uri=${window.location.origin}/#/oauth2/redirect`;
        break;
      default:
        this.toastr.warning(`Provider ${provider.toUpperCase()} will be available soon`);
        break;
    }
  }
}
