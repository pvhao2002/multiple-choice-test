import {Component, EventEmitter, OnDestroy, OnInit, Output, signal, ViewChild, viewChild} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {PaginationComponent} from 'ngx-bootstrap/pagination';
import {PagingData, ResponseData} from '../../../shared/model/response-data.model';
import {UserInfo} from '../../../shared/model/profile.model';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {BsModalRef, BsModalService} from 'ngx-bootstrap/modal';
import {forkJoin} from 'rxjs';
import {NgOptgroupTemplateDirective, NgSelectComponent} from '@ng-select/ng-select';

@Component({
  selector: 'app-add-student-course',
  imports: [
    FormsModule,
    PaginationComponent,
    NgSelectComponent,
    NgOptgroupTemplateDirective
  ],
  templateUrl: './add-student-course.component.html',
  standalone: true,
  styleUrl: './add-student-course.component.scss'
})
export class AddStudentCourseComponent implements OnInit, OnDestroy {
  @ViewChild('student_fail') listAddStudentFail: any;
  data: PagingData<UserInfo> = new PagingData<UserInfo>();
  dataStudent: UserInfo[] = [];
  courseId = signal(-1);
  selectedStudent: string[] = [];
  listStudentFail: any = [];
  @Output() event = new EventEmitter<boolean>();

  constructor(private http: HttpClient,
              private toast: ToastrService,
              private bsRef: BsModalRef,
              private bsModal: BsModalService
  ) {
  }

  ngOnInit(): void {
    this.initData();
  }

  initData() {
    forkJoin([
      this.http.get<ResponseData<UserInfo[]>>('api/users/all'),
      this.http.get<ResponseData<PagingData<UserInfo>>>(`api/courses/${this.courseId()}/student?page=${this.data.page}&size=${this.data.size}`)
    ]).subscribe(([res1, res2]) => {
      if (res1.success) {
        this.dataStudent = res1.data;
      } else {
        this.toast.error(res1.message);
      }

      if (res2.success) {
        this.data = res2.data;
      } else {
        this.toast.error(res2.message);
      }
    });
  }

  addStudent() {
    const params = {
      courseId: this.courseId(),
      studentIds: this.selectedStudent
    };
    this.http.post<ResponseData<string>>('api/courses/add-student', params)
      .subscribe(res => {
        if (res.success) {
          this.toast.success('Add student success');
          this.getData();
        } else {
          this.listStudentFail = res.data;
          this.bsModal.show(this.listAddStudentFail, {
            class: 'modal-lg',
          });
        }
      });
  }

  delete(item: UserInfo) {
    this.http.delete<ResponseData<string>>(`api/courses/${this.courseId()}/student/${item.studentId}`)
      .subscribe(res => {
        if (res.success) {
          this.toast.success('Delete student success');
          this.getData();
        } else {
          this.toast.error('Delete student fail');
        }
      });
  }

  getData() {
    this.http.get<ResponseData<PagingData<UserInfo>>>(`api/courses/${this.courseId()}/student?page=${this.data.page}&size=${this.data.size}`)
      .subscribe(res => {
        if (res.success) {
          this.data = res.data;
        } else {
          this.toast.error(res.message);
        }
      });
  }

  pageChanged(event: any) {
    this.data.page = event.page;
    this.getData();
  }

  ngOnDestroy(): void {
    this.event.emit(true);
  }
}
