import {Component, OnInit, signal, ViewChild} from '@angular/core';
import {PageTitleComponent} from '../../page-title/page-title.component';
import {Breadcumb} from '../../../shared/model/breadcumb';
import {HttpClient} from '@angular/common/http';
import {User} from '../../../shared/model/user';
import {PagingData, ResponseData} from '../../../shared/model/response-data.model';
import {ENUM} from '../../../shared/model/Enums';
import {ToastrService} from 'ngx-toastr';
import {PageChangedEvent, PaginationComponent} from 'ngx-bootstrap/pagination';
import {FormsModule} from '@angular/forms';
import {debounceTime, Subject} from 'rxjs';
import {CONSTANT} from '../../../shared/constant';
import {ExportDataService} from '../../../shared/service/export-data.service';
import {BsModalService} from 'ngx-bootstrap/modal';
import {AddStudentComponent} from '../add-student/add-student.component';

@Component({
  selector: 'app-user-management',
  imports: [
    PageTitleComponent,
    PaginationComponent,
    FormsModule,
  ],
  templateUrl: './user-management.component.html',
  standalone: true,
  styleUrls: ['./user-management.component.scss', CONSTANT.lib1, CONSTANT.lib2]
})
export class UserManagementComponent implements OnInit {
  @ViewChild('student_fail') studentFail: any;
  breadCrumbs = [
    new Breadcumb('Home', '/'),
    new Breadcumb('User Management', '/admin/user-management'),
    new Breadcumb('List User', '/')
  ];
  data: PagingData<User> = new PagingData<User>();
  textSearch = signal('');
  searchSubject = new Subject<string>();
  listStudentFail: string[] = [];

  constructor(
    private http: HttpClient,
    private toast: ToastrService,
    private exportService: ExportDataService,
    private bsModal: BsModalService
  ) {
    this.searchSubject.pipe(
      debounceTime(500) // Wait for 500ms before calling API
    ).subscribe(() => this.getListUser());
  }

  ngOnInit(): void {
    this.getListUser();
  }

  getListUser(page = 1, size = 10) {
    this.http.get<ResponseData<PagingData<User>>>(`/api/users?page=${page}&size=${size}&key=${this.textSearch()}`)
      .subscribe(res => {
        if (res.success) {
          this.data = res.data;
        }
      });
  }

  updateStatus(userId: number, status: boolean, statusStr: string) {
    if ( // case status is not change => return
      (status && statusStr === ENUM.STATUS.ACTIVE) ||
      (!status && statusStr === ENUM.STATUS.INACTIVE)
    ) {
      return;
    }
    this.http.post<ResponseData<string>>(`/api/users/change-status?userId=${userId}&status=${status}`, {})
      .subscribe(res => {
        if (res.success) {
          this.toast.success(res.message, 'Update status successful');
          this.getListUser(this.data.page, this.data.size);
        } else {
          this.toast.error(res.message, 'Update status failed');
        }
      });
  }

  onSearch() {
    this.searchSubject.next(this.textSearch());
  }

  getStatus(status: string) {
    switch (status.toUpperCase()) {
      case ENUM.STATUS.ACTIVE:
        return ENUM.RESPONSE.SUCCESS.toLowerCase();
      case ENUM.STATUS.INACTIVE:
        return ENUM.RESPONSE.DANGER.toLowerCase();
      default:
        return ENUM.RESPONSE.WARNING.toLowerCase();
    }
  }

  pageChanged($event: PageChangedEvent) {
    this.getListUser($event.page, $event.itemsPerPage);
  }

  exportExcel() {
    window.open('http://localhost:1122/api/users/export', '_blank');
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];

      // TODO: Implement file upload logic here
      this.uploadFile(file);
    }
  }

  uploadFile(file: File): void {
    const formData = new FormData();
    formData.append('file', file);

    this.http.post<ResponseData<any>>('api/users/import', formData)
      .subscribe(res => {
        if (res.success) {
          this.toast.success('Import successfully');
        } else {
          if (res.status === 400) {
            this.toast.error(res.message);
          } else {
            this.listStudentFail = res.data;
            this.bsModal.show(this.studentFail, {
              class: 'modal-lg'
            });
          }
        }
      });
  }

  addStudent() {
    this.bsModal.show(AddStudentComponent, {
      class: 'modal-lg modal-dialog-centered',
    });
  }
}
