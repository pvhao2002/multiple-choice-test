import {Component, OnInit, signal} from '@angular/core';
import {PagingData, ResponseData} from '../../../shared/model/response-data.model';
import {ResponseResult, Result} from '../../../shared/model/Result';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {DatePipe} from '@angular/common';
import {debounceTime, Subject} from 'rxjs';
import {FormsModule} from '@angular/forms';
import {PaginationComponent} from 'ngx-bootstrap/pagination';
import {Router} from '@angular/router';

@Component({
  selector: 'app-learning-result',
  imports: [
    DatePipe,
    FormsModule,
    PaginationComponent
  ],
  templateUrl: './learning-result.component.html',
  standalone: true,
  styleUrl: './learning-result.component.scss'
})
export class LearningResultComponent implements OnInit {
  data1: PagingData<Result> = new PagingData<Result>();
  data2: PagingData<Result> = new PagingData<Result>();
  textSearch = signal('');
  searchSubject = new Subject<string>();

  constructor(private http: HttpClient,
              private toast: ToastrService,
              private router: Router,
  ) {
  }

  ngOnInit(): void {
    this.getList();
    this.searchSubject.pipe(
      debounceTime(500) // Wait for 500ms before calling API
    ).subscribe(() => this.getList());
  }

  getList(page1: number = this.data1.page, page2: number = this.data2.page) {
    this.http.get<ResponseData<ResponseResult>>(`/api/v2/test/result?page1=${page1}&page2=${page2}&key=${this.textSearch()}`)
      .subscribe(res => {
        if (res.success) {
          this.data1 = res.data.complete;
          this.data2 = res.data.incomplete;
        } else {
          this.toast.error(`Server error`);
        }
      });
  }

  onSearch() {
    this.searchSubject.next(this.textSearch());
  }

  pageChanged2(event: any) {
    this.getList(this.data1.page, event.page);
  }

  pageChanged1(event: any) {
    this.getList(event.page, this.data2.page);
  }

  navigateReview(tid: number) {
    this.router.navigate(['/student/start'], {
      queryParams: {
        tid: tid,
        review: 'true'
      }
    }).then();
  }

  navigateTestContinue(eid: number) {
    this.router.navigate(['/student/start'], {
      queryParams: {
        eid: eid
      }
    })
      .then();
  }
}
