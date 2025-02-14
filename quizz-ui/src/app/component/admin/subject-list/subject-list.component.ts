import {Component, OnInit, signal} from '@angular/core';
import {Breadcumb} from '../../../shared/model/breadcumb';
import {PageTitleComponent} from '../../page-title/page-title.component';
import {CONSTANT} from '../../../shared/constant';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {BsModalService} from 'ngx-bootstrap/modal';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {PagingData, ResponseData} from '../../../shared/model/response-data.model';
import {SubjectDTO} from '../../../shared/model/subject';
import {debounceTime, Subject} from 'rxjs';

@Component({
  selector: 'app-subject-list',
  imports: [
    PageTitleComponent,
    ReactiveFormsModule,
    FormsModule
  ],
  templateUrl: './subject-list.component.html',
  standalone: true,
  styleUrls: ['./subject-list.component.scss', CONSTANT.lib1, CONSTANT.lib2]
})
export class SubjectListComponent implements OnInit {
  breadCrumbs = [
    new Breadcumb('Home', '/'),
    new Breadcumb('Subject Management', '#/subject'),
    new Breadcumb('List Subject', '#/subject/list')
  ];
  textSearch = signal('');
  searchSubject = new Subject<string>();
  data: PagingData<SubjectDTO> = new PagingData<SubjectDTO>();

  constructor(
    private http: HttpClient
    , private toast: ToastrService
    , private bsModal: BsModalService
  ) {
    this.searchSubject.pipe(
      debounceTime(500) // Wait for 500ms before calling API
    ).subscribe(() => this.getListSubject());
  }

  ngOnInit(): void {
    this.getListSubject();
  }

  getListSubject(page: number = this.data.page, size: number = this.data.size) {
    this.http.get<ResponseData<PagingData<SubjectDTO>>>(`api/subjects?page=${page}&size=${size}&key=${this.textSearch()}`)
      .subscribe(res => {
        this.data = res.data;
      });
  }

  onSearch() {
    this.searchSubject.next(this.textSearch());
  }
}
