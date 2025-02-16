import {Component, OnInit, signal} from '@angular/core';
import {Breadcumb} from '../../../shared/model/breadcumb';
import {PageTitleComponent} from '../../page-title/page-title.component';
import {CONSTANT, DEFAULT_PAGING_CONFIG} from '../../../shared/constant';
import {HttpClient} from '@angular/common/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {PagingData, ResponseData} from '../../../shared/model/response-data.model';
import {SubjectDTO} from '../../../shared/model/subject';
import {debounceTime, Subject} from 'rxjs';
import {Router} from '@angular/router';
import {PageChangedEvent, PaginationComponent} from 'ngx-bootstrap/pagination';
import {BsModalService} from 'ngx-bootstrap/modal';
import {SubjectUpsertComponent} from '../subject-upsert/subject-upsert.component';
import {ConfirmComponent} from '../../confirm/confirm.component';

@Component({
  selector: 'app-subject-list',
  imports: [
    PageTitleComponent,
    ReactiveFormsModule,
    FormsModule,
    PaginationComponent
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
    , private router: Router
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

  openCreate() {
    this.router.navigate(['admin/subject/upsert']).then(r => r);
  }

  refresh() {
    this.textSearch.set('');
    this.getListSubject(DEFAULT_PAGING_CONFIG.pageNo, DEFAULT_PAGING_CONFIG.pageSize);
  }

  pageChanged($event: PageChangedEvent) {
    this.getListSubject($event.page, $event.itemsPerPage);
  }

  updateSubject(subject: SubjectDTO) {
    const bsRef = this.bsModal.show(SubjectUpsertComponent, {
      class: 'modal-lg modal-dialog-centered',
      initialState: {
        isPopupVisible: signal(true),
        subject: signal(subject),
      }
    });
    bsRef.content?.eventOut?.subscribe(res => {
      if (res) {
        this.getListSubject();
      }
    });
  }

  deleteSubject(subject: SubjectDTO) {
    const bsRef = this.bsModal.show(ConfirmComponent, {
      class: 'modal-dialog-centered',
    });

    bsRef.content?.confirmed.subscribe((res: boolean) => {
      if (res) {
        this.http.delete(`api/subjects?subId=${subject.subjectId}`)
          .subscribe(() => {
            this.getListSubject();
          });
      }
    })
  }
}
