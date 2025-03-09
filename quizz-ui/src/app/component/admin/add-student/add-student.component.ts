import {Component, EventEmitter, OnDestroy, OnInit, Output, signal} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {BsModalRef} from 'ngx-bootstrap/modal';
import {UserInfo} from '../../../shared/model/profile.model';
import {ResponseData} from '../../../shared/model/response-data.model';

@Component({
  selector: 'app-add-student',
  imports: [
    ReactiveFormsModule,
    FormsModule
  ],
  templateUrl: './add-student.component.html',
  standalone: true,
  styleUrl: './add-student.component.scss'
})
export class AddStudentComponent implements OnInit, OnDestroy {
  isEdit = signal(false);
  title = signal('Add Student');
  @Output() event = new EventEmitter<boolean>();
  user: UserInfo = new UserInfo();

  constructor(private http: HttpClient,
              private toast: ToastrService,
              private bsRef: BsModalRef
  ) {
  }

  ngOnInit(): void {
  }

  closePopup() {
    this.bsRef.hide();
  }

  ngOnDestroy(): void {
    this.event.next(true);
  }

  submit() {
    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(this.user)], {type: 'application/json'}));
    this.http.post<ResponseData<string>>('api/users/create', formData)
      .subscribe(res => {
        if (res.success) {
          this.toast.success('Add student success');
          this.event.next(true);
          this.bsRef.hide();
        } else {
          this.toast.error(res.message);
        }
      })
  }
}
