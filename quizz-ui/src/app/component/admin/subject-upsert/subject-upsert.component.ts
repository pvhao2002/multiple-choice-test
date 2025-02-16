import {Component, EventEmitter, Output, output, signal} from '@angular/core';
import {PageTitleComponent} from '../../page-title/page-title.component';
import {Breadcumb} from '../../../shared/model/breadcumb';
import {SubjectDTO} from '../../../shared/model/subject';
import {FormsModule} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {TransferFileService} from '../../../shared/service/transfer-file.service';
import {ToastrService} from 'ngx-toastr';
import {ResponseData} from '../../../shared/model/response-data.model';
import {BsModalRef} from 'ngx-bootstrap/modal';

@Component({
  selector: 'app-subject-upsert',
  imports: [
    PageTitleComponent,
    FormsModule
  ],
  templateUrl: './subject-upsert.component.html',
  standalone: true,
  providers: [TransferFileService],
  styleUrl: './subject-upsert.component.scss'
})
export class SubjectUpsertComponent {
  isPopupVisible = signal(false);
  breadCrumbs = [
    new Breadcumb('Home', '/'),
    new Breadcumb('Subject Management', '/admin/subject'),
    new Breadcumb('Create Subject', '/admin/upsert')
  ];

  subject = signal(new SubjectDTO());
  @Output() eventOut = new EventEmitter<boolean>();

  constructor(
    private http: HttpClient,
    protected fileService: TransferFileService,
    private toast: ToastrService,
    private bsRef: BsModalRef
  ) {
  }

  submit() {
    if (this.isPopupVisible()) {
      this.update();
    } else {
      this.create();
    }
  }

  create() {
    if (this.subject().name === '') {
      this.toast.error('Subject name is required');
      return;
    }
    if (this.fileService.fileData === undefined) {
      this.toast.error('Subject image is required');
      return;
    }

    const formData = new FormData();
    formData.append('name', this.subject().name);
    formData.append('file', this.fileService.fileData);
    this.http.post<ResponseData<string>>('/api/subjects', formData)
      .subscribe({
        next: (res: ResponseData<string>) => {
          if (res.success) {
            this.toast.success('Create subject successfully');
            this.subject.set(new SubjectDTO());
            this.fileService.reset();
          } else {
            this.toast.error(res.message);
          }
        }
      });
  }

  update() {
    if (this.subject().name === '') {
      this.toast.error('Subject name is required');
      return;
    }
    const formData = new FormData();
    formData.append('subId', this.subject().subjectId.toString());
    formData.append('name', this.subject().name);
    if (this.fileService.fileData) {
      formData.append('file', this.fileService.fileData);
    }
    this.http.patch<ResponseData<string>>('api/subjects', formData)
      .subscribe(res => {
        if (res.success) {
          this.toast.success('Update subject successfully');
          this.bsRef.hide();
          this.eventOut.emit(true);
        } else {
          this.toast.error(res.message);
        }
      });
  }

  clearUpload() {
    this.fileService.reset();
  }
}
