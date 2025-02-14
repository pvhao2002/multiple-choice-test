import {Component} from '@angular/core';
import {CONSTANT} from '../../../shared/constant';
import {PageTitleComponent} from '../../page-title/page-title.component';
import {Breadcumb} from '../../../shared/model/breadcumb';
import {SubjectDTO} from '../../../shared/model/subject';
import {FormsModule} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {TransferFileService} from '../../../shared/service/transfer-file.service';
import {ToastrService} from 'ngx-toastr';
import {ResponseData} from '../../../shared/model/response-data.model';

@Component({
  selector: 'app-subject-upsert',
  imports: [
    PageTitleComponent,
    FormsModule
  ],
  templateUrl: './subject-upsert.component.html',
  standalone: true,
  styleUrls: ['./subject-upsert.component.scss', CONSTANT.lib1, CONSTANT.lib2]
})
export class SubjectUpsertComponent {
  breadCrumbs = [
    new Breadcumb('Home', '/'),
    new Breadcumb('Subject Management', '/admin/subject'),
    new Breadcumb('Create Subject', '/admin/upsert')
  ];

  subject: SubjectDTO = new SubjectDTO();

  constructor(
    private http: HttpClient,
    protected fileService: TransferFileService,
    private toast: ToastrService,
  ) {
  }

  submit() {
    if (this.subject.name === '') {
      this.toast.error('Subject name is required');
      return;
    }
    if (this.fileService.fileData === undefined) {
      this.toast.error('Subject image is required');
      return;
    }

    const formData = new FormData();
    formData.append('name', this.subject.name);
    formData.append('file', this.fileService.fileData);
    this.http.post<ResponseData<string>>('/api/subjects', formData)
      .subscribe({
        next: (res: ResponseData<string>) => {
          if (res.success) {
            this.toast.success('Create subject successfully');
            this.subject = new SubjectDTO();
            this.fileService.reset();
          } else {
            this.toast.error(res.message);
          }
        }
      });
  }
}
