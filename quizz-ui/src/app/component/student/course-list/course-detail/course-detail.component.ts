import {Component, OnInit, signal} from '@angular/core';
import {Breadcumb} from '../../../../shared/model/breadcumb';
import {PageTitleComponent} from '../../../page-title/page-title.component';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute, Router} from '@angular/router';
import {CourseDetail, ExamCourse, SubjectCourse} from '../../../../shared/model/CourseDetail';
import {DatePipe, NgClass} from '@angular/common';
import {ResponseData} from '../../../../shared/model/response-data.model';
import {ToastrService} from 'ngx-toastr';
import {Exam} from '../../../../shared/model/Exam';
import {BsModalService} from 'ngx-bootstrap/modal';
import {ConfirmV2Component} from '../../../confirm-v2/confirm-v2.component';

@Component({
  selector: 'app-course-detail',
  imports: [
    PageTitleComponent,
    NgClass,
    DatePipe
  ],
  templateUrl: './course-detail.component.html',
  standalone: true,
  styleUrl: './course-detail.component.scss'
})
export class CourseDetailComponent implements OnInit {
  breadCrumbs = [
    new Breadcumb('Home', '/'),
    new Breadcumb('Course List', '/student/course-detail'),
  ];
  courseId: number = 0;
  courseDetail: CourseDetail = new CourseDetail();

  constructor(private http: HttpClient,
              private activeRoute: ActivatedRoute,
              private toast: ToastrService,
              private bsModal: BsModalService,
              private router: Router
  ) {
  }

  ngOnInit(): void {
    this.activeRoute.queryParams.subscribe(params => {
      this.courseId = params['cid'];
      this.getData();
    });
  }

  getData() {
    this.http.get<ResponseData<CourseDetail>>(`api/courses/detail?cid=${this.courseId}`)
      .subscribe(res => {
        if (res.success) {
          this.courseDetail = res.data;
        } else {
          this.toast.error(res.message);
        }
      });
  }

  expand(item: SubjectCourse) {
    item.isExpanded = !item.isExpanded;
  }

  startTest(e: ExamCourse) {
    // check if exam is not available: not started yet or expired
    if (new Date(e.startDate) > new Date()) {
      this.toast.error('This test is not available yet');
      return;
    } else if (new Date(e.endDate) < new Date()) {
      this.toast.error('This test is expired');
      return;
    }

    this.bsModal.show(ConfirmV2Component, {
      class: 'modal-dialog-centered modal-sm',
      initialState: {
        title: signal('Start test'),
        message: signal('Do you want to start this test?')
      }
    }).content?.onClick.subscribe(isConfirm => {
      if (isConfirm) {
        this.router.navigate(['/student/start'], {
          queryParams: {
            eid: e.testId
          }
        }).then();
      }
    });
  }

}

