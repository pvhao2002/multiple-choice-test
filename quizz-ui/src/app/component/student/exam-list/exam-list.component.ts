import {Component, OnInit} from '@angular/core';
import {Breadcumb} from '../../../shared/model/breadcumb';
import {PageTitleComponent} from '../../page-title/page-title.component';
import {NgClass} from '@angular/common';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {CONSTANT} from '../../../shared/constant';
import {PagingData, ResponseData} from '../../../shared/model/response-data.model';
import {ActivatedRoute, Router} from '@angular/router';
import {Exam} from '../../../shared/model/Exam';
import {PaginationComponent} from 'ngx-bootstrap/pagination';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-exam-list',
  imports: [
    PageTitleComponent,
    NgClass,
    PaginationComponent,
    FormsModule
  ],
  templateUrl: './exam-list.component.html',
  standalone: true,
  styleUrls: ['./exam-list.component.scss', CONSTANT.lib1, CONSTANT.lib2],
})
export class ExamListComponent implements OnInit {
  breadCrumbs = [
    new Breadcumb('Home', '/'),
    new Breadcumb('Exam List', '/student/exam'),
  ];
  model: Mode = Mode.ALL;
  data: PagingData<Exam> = new PagingData<Exam>();
  subjectId: number = -1;

  constructor(private http: HttpClient,
              private toast: ToastrService,
              private route: ActivatedRoute,
              private router: Router
  ) {
  }


  protected readonly Mode = Mode;

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params['sid']) {
        this.subjectId = params['sid'];
      }
      this.getList();
    });
  }

  switch(mode: Mode) {
    this.model = mode;
    this.getList();
  }

  getList(page: number = this.data.page, size: number = this.data.size) {
    this.http.get<ResponseData<PagingData<Exam>>>(`api/v2/test?page=${page}&size=${size}&sid=${this.subjectId}&mode=${this.model}`)
      .subscribe(res => {
        if (res.success) {
          this.data = res.data;
        } else {
          this.toast.error(res.message);
        }
      });
  }

  pageChanged(event: any): void {
    this.getList(event.page, this.data.size);
  }

  startTest(exam: Exam) {
    this.router.navigate(['/student/start'], {
      queryParams: {
        eid: exam.testId
      }
    }).then();
  }
}


export enum Mode {
  ALL = -1,
  YES = 1,
  NO = 0
}
