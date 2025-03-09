import {Component, OnInit} from '@angular/core';
import {Breadcumb} from '../../../shared/model/breadcumb';
import {HttpClient} from '@angular/common/http';
import {PageTitleComponent} from '../../page-title/page-title.component';
import {PagingData, ResponseData} from '../../../shared/model/response-data.model';
import {SubjectDTO} from '../../../shared/model/subject';
import {Router} from '@angular/router';
import {PaginationComponent} from 'ngx-bootstrap/pagination';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-subject-list',
  imports: [
    PageTitleComponent,
    PaginationComponent,
    FormsModule
  ],
  templateUrl: './subject-list.component.html',
  standalone: true,
  styleUrl: './subject-list.component.scss'
})
export class SubjectListComponent implements OnInit {
  breadCrumbs = [
    new Breadcumb('Home', '/'),
    new Breadcumb('Subject List', '/student/subject'),
  ];
  data: PagingData<SubjectDTO> = new PagingData<SubjectDTO>();

  constructor(private http: HttpClient,
              private router: Router
  ) {
  }

  ngOnInit(): void {
    this.getListSubject();
  }

  getListSubject(page: number = this.data.page, size: number = this.data.size) {
    this.http.get<ResponseData<PagingData<SubjectDTO>>>(`api/v2/subjects?page=${page}&size=${size}`)
      .subscribe(res => {
        this.data = res.data;
      });
  }

  listExam(subjectId: number) {
    this.router.navigate(['/student/exam'], {queryParams: {sid: subjectId}}).then();
  }

  pageChanged(event: any): void {
    this.getListSubject(event.page, event.itemsPerPage);
  }
}
