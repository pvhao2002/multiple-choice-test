import {Component, signal} from '@angular/core';
import {CodeInputModule} from 'angular-code-input';
import {ToastrService} from 'ngx-toastr';
import {HttpClient} from '@angular/common/http';
import {ResponseData} from '../../shared/model/response-data.model';
import {OtpVerify} from '../../shared/model/otp-verify';
import {BsModalRef} from 'ngx-bootstrap/modal';
import {Router} from '@angular/router';

@Component({
  selector: 'app-pincode',
  imports: [
    CodeInputModule
  ],
  templateUrl: './pincode.component.html',
  standalone: true,
  styleUrl: './pincode.component.scss'
})
export class PincodeComponent {
  email = signal('');
  code = '';
  otpPayload: OtpVerify = new OtpVerify();

  constructor(private toastr: ToastrService,
              private http: HttpClient,
              private bsRef: BsModalRef,
              private router: Router
  ) {
  }

  onCodeCompleted($event: any) {
    this.code = $event;
  }

  verifyCode() {
    if (this.code.length !== 6) {
      this.toastr.error('Code must be 6 digits');
      return;
    }
    this.otpPayload.email = this.email();
    this.otpPayload.otp = this.code
    this.http.post<ResponseData<string>>('/api/auth/otp-verify', this.otpPayload)
      .subscribe({
        next: (data: any) => {
          if (data.success) {
            this.toastr.success(data.data);
            this.bsRef.hide();
            this.router.navigate(['/login']).then();
          } else {
            this.toastr.error(data.message);
          }
        }
      });
  }

  resendCode() {
    this.http.post<ResponseData<string>>('/api/auth/otp-resend', this.email())
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
}
