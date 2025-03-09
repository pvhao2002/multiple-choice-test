import {Component, OnInit} from '@angular/core';
import {Breadcumb} from '../../../shared/model/breadcumb';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {PageTitleComponent} from '../../page-title/page-title.component';
import {PaginationComponent} from 'ngx-bootstrap/pagination';
import {PagingData, ResponseData} from '../../../shared/model/response-data.model';
import {Course} from '../../../shared/model/Course';
import {FormsModule} from '@angular/forms';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-course-list',
  imports: [
    PageTitleComponent,
    PaginationComponent,
    FormsModule
  ],
  templateUrl: './course-list.component.html',
  standalone: true,
  styleUrl: './course-list.component.scss'
})
export class CourseListComponent implements OnInit {
  breadCrumbs = [
    new Breadcumb('Home', '/'),
    new Breadcumb('Course List', '/student/course'),
  ];
  data: PagingData<Course> = new PagingData<Course>();

  constructor(private http: HttpClient,
              private router: Router,
              private toast: ToastrService
  ) {
  }

  ngOnInit(): void {
    this.getData();
  }

  getData(page: number = this.data.page, size: number = this.data.size) {
    this.http.get<ResponseData<PagingData<Course>>>(`api/courses/student?page=${page}&size=${size}`)
      .subscribe(res => {
        if (res.success) {
          this.data = res.data;
        } else {
          this.toast.error(res.message);
        }
      });
  }

  openDetail(e: Course) {
    this.router.navigate(['/student/course-detail'], {queryParams: {cid: e.courseId}}).then();
  }

  pageChanged(event: any): void {
    this.getData(event.page, event.itemsPerPage);
  }
}
