import {Component, OnInit, signal} from '@angular/core';
import {NgOptimizedImage} from '@angular/common';
import {ProfileDTO} from '../../shared/model/profile.model';
import {HttpClient} from '@angular/common/http';
import {ErrorMessage} from '../../shared/model/ErrorMessage';
import {FormsModule} from '@angular/forms';
import {ToastrService} from 'ngx-toastr';
import {CodeInputModule} from 'angular-code-input';
import {BsModalService} from 'ngx-bootstrap/modal';
import {ResponseData} from '../../shared/model/response-data.model';
import {CONSTANT} from '../../shared/constant';
import {AuthenticationService} from '../../shared/service/authentication.service';
import {PincodeComponent} from '../pincode/pincode.component';
import {Router} from '@angular/router';

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
  model: ProfileDTO = new ProfileDTO();
  error: ErrorMessage = new ErrorMessage('', '');
  hasCheckboxes = false;


  constructor(
    private http: HttpClient
    , private toastr: ToastrService
    , private bsModelService: BsModalService
    , private authService: AuthenticationService
    , private router: Router
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
            this.bsModelService.show(PincodeComponent, {
              class: 'modal-lg modal-dialog-centered',
              initialState: {
                email: signal(this.model.email)
              }
            });
          } else {
            this.toastr.error(data.message);
          }
        }
      });
  }

  navigateToLogin() {
    this.router.navigate(['/login']).then();
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


