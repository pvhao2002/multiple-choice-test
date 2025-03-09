import {Component, EventEmitter, OnDestroy, OnInit, Output, signal, ViewChild} from '@angular/core';
import {NgSelectComponent} from '@ng-select/ng-select';
import {PaginationComponent} from 'ngx-bootstrap/pagination';
import {PagingData, ResponseData} from '../../../shared/model/response-data.model';
import {UserInfo} from '../../../shared/model/profile.model';
import {HttpClient} from '@angular/common/http';
import {BsModalService} from 'ngx-bootstrap/modal';
import {Exam} from '../../../shared/model/Exam';
import {forkJoin} from 'rxjs';
import {FormsModule} from '@angular/forms';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-add-exam-course',
  imports: [
    NgSelectComponent,
    PaginationComponent,
    FormsModule
  ],
  templateUrl: './add-exam-course.component.html',
  standalone: true,
  styleUrl: './add-exam-course.component.scss'
})
export class AddExamCourseComponent implements OnInit, OnDestroy {
  @ViewChild('exam_fail') examFail: any;
  data: PagingData<Exam> = new PagingData<Exam>();
  dataExam: Exam[] = [];
  courseId = signal(-1);
  selectedExam: string[] = [];
  @Output() event = new EventEmitter<boolean>();
  listExamFail: any = [];

  constructor(private http: HttpClient,
              private bsModal: BsModalService,
              private toast: ToastrService
  ) {
  }

  ngOnDestroy(): void {
    this.event.emit(true);
  }

  ngOnInit(): void {
    this.initData();
  }

  initData() {
    forkJoin([
      this.http.get<ResponseData<PagingData<Exam>>>('api/test?size=100000'),
      this.http.get<ResponseData<PagingData<Exam>>>(`api/courses/${this.courseId()}/exam?page=${this.data.page}&size=${this.data.size}`)
    ]).subscribe(([res1, res2]: ResponseData<PagingData<Exam>>[]) => {
      if (res1.success) {
        this.dataExam = res1.data.contents;
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

  addExam() {
    console.log('12313')
    const params = {
      courseId: this.courseId(),
      examIds: this.selectedExam
    };
    this.http.post<ResponseData<string>>('api/courses/add-exam', params)
      .subscribe(res => {
        if (res.success) {
          this.toast.success('Add Exam success');
          this.getData();
        } else {
          this.listExamFail = res.data;
          this.bsModal.show(this.examFail, {
            class: 'modal-lg',
          });
        }
      });
  }

  delete(item: Exam) {
    this.http.delete<ResponseData<string>>(`api/courses/${this.courseId()}/exam/${item.testId}`)
      .subscribe(res => {
        if (res.success) {
          this.toast.success('Delete Exam success');
          this.getData();
        } else {
          this.toast.error(res.message);
        }
      });
  }

  pageChanged(event: any) {
    this.data.page = event.page;
    this.initData();
  }

  getData() {
    this.http.get<ResponseData<PagingData<Exam>>>(`api/courses/${this.courseId()}/exam?page=${this.data.page}&size=${this.data.size}`)
      .subscribe(res => {
        if (res.success) {
          this.data = res.data;
        } else {
          this.toast.error(res.message);
        }
      });
  }
}
