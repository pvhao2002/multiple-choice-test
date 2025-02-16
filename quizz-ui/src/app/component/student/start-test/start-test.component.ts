import {Component, OnInit} from '@angular/core';
import {Breadcumb} from '../../../shared/model/breadcumb';
import {PageTitleComponent} from '../../page-title/page-title.component';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute, Router} from '@angular/router';
import {Question} from '../../../shared/model/Exam';
import {ResponseData} from '../../../shared/model/response-data.model';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-start-test',
  imports: [
    PageTitleComponent
  ],
  templateUrl: './start-test.component.html',
  standalone: true,
  styleUrl: './start-test.component.scss'
})
export class StartTestComponent implements OnInit {
  breadCrumbs = [
    new Breadcumb('Home', '/'),
    new Breadcumb('Start Test', '/student/start'),
  ];
  data: Question[] = [];
  t = Array.from({length: 30}, (v, k) => k + 1);

  constructor(private http: HttpClient,
              private route: ActivatedRoute,
              private router: Router,
              private toast: ToastrService
  ) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(param => {
      const examId = param['eid'];
      if (!examId) {
        this.router.navigate(['/student/exam']).then();
      } else {
        this.getTestDetail(examId);
      }
    });
  }

  getTestDetail(eid: number) {
    this.http.get<ResponseData<Question[]>>(`api/test/detail?eid=${eid}`)
      .subscribe(res => {
        if (res.success) {
          this.data = res.data;
        } else {
          this.toast.error(res.message);
          this.router.navigate(['/student/exam']).then();
        }
      });
  }
}
