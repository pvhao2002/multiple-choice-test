import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {NgOptimizedImage} from '@angular/common';
import {ProfileDTO} from '../../shared/model/profile.model';
import {HttpClient} from '@angular/common/http';
import {ErrorMessage} from '../../shared/model/ErrorMessage';
import {FormsModule} from '@angular/forms';
import {ToastrService} from 'ngx-toastr';
import {CodeInputModule} from 'angular-code-input';
import {BsModalService} from 'ngx-bootstrap/modal';
import {ResponseData} from '../../shared/model/response-data.model';
import {OtpVerify} from '../../shared/model/otp-verify';
import {CONSTANT} from '../../shared/constant';
import {AuthenticationService} from '../../shared/service/authentication.service';

@Component({
  selector: 'app-register',
  imports: [
    NgOptimizedImage,
    FormsModule,
    CodeInputModule,
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
  standalone: true,
})
export class RegisterComponent implements OnInit {
  @ViewChild('pincode') pinCode!: TemplateRef<any>;
  model: ProfileDTO = new ProfileDTO();
  error: ErrorMessage = new ErrorMessage('', '');
  hasCheckboxes = false;
  code = '';
  otpPayload: OtpVerify = new OtpVerify();

  constructor(
    private http: HttpClient
    , private toastr: ToastrService
    , private bsModelService: BsModalService
    , private authService: AuthenticationService
  ) {
  }

  ngOnInit(): void {
    if (this.authService.isLoggedIn) {
      this.authService.redirectHome();
    }
  }

  signUp() {
    this.http.post<ResponseData<string>>('/api/auth/register', this.model)
      .subscribe({
        next: (data: any) => {
          if (data.success) {
            this.toastr.success(data.data);
            this.bsModelService.show(this.pinCode, {
              class: 'modal-lg modal-dialog-centered',
            });
          } else {
            this.toastr.error(data.message);
          }
        }
      });
  }


  onCodeCompleted($event: string) {
    this.code = $event;
  }

  verifyCode() {
    if (this.code.length !== 6) {
      this.toastr.error('Code must be 6 digits');
      return;
    }
    this.otpPayload.email = 'hideonbush8405+test11@gmail.com';
    this.otpPayload.otp = this.code
    this.http.post<ResponseData<string>>('/api/auth/otp-verify', this.otpPayload)
      .subscribe({
        next: (data: any) => {
          if (data.success) {
            this.toastr.success(data.data);
            this.bsModelService.hide();
          } else {
            this.toastr.error(data.message);
          }
        }
      });
  }

  resendCode() {
    this.http.post<ResponseData<string>>('/api/auth/otp-resend', 'hideonbush8405+test11@gmail.com')
      .subscribe({
        next: (data: any) => {
          if (data.success) {
            this.toastr.success(data.data);
          } else {
            this.toastr.error(data.message);
          }
        }
      });
  }

  loginWithGoogle() {
    window.location.href = `${CONSTANT.BE_URL_LOCAL}/api/oauth2/authorize/google?redirect_uri=${window.location.origin}/oauth2/redirect`;
  }
}


