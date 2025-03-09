import {Component, OnInit, signal} from '@angular/core';
import {Breadcumb} from '../../../shared/model/breadcumb';
import {PageTitleComponent} from '../../page-title/page-title.component';
import {FormsModule} from '@angular/forms';
import {PaginationComponent} from 'ngx-bootstrap/pagination';
import {debounceTime, Subject} from 'rxjs';
import {PagingData, ResponseData} from '../../../shared/model/response-data.model';
import {Exam} from '../../../shared/model/Exam';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {BsModalService} from 'ngx-bootstrap/modal';
import {UpdateExamComponent} from '../update-exam/update-exam.component';
import {ConfirmComponent} from '../../confirm/confirm.component';
import {ImportExcelExamComponent} from './import-excel-exam/import-excel-exam.component';
import {ToastrService} from 'ngx-toastr';
import {RandomTestComponent} from './random-test/random-test.component';
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-exam-list',
  imports: [
    PageTitleComponent,
    FormsModule,
    PaginationComponent,
    DatePipe
  ],
  templateUrl: './exam-list.component.html',
  standalone: true,
  styleUrl: './exam-list.component.scss'
})
export class ExamListComponent implements OnInit {
  breadCrumbs = [
    new Breadcumb('Home', '/'),
    new Breadcumb('Exam Management', '/admin/exam'),
    new Breadcumb('List Exam', '/admin/exam/list')
  ];

  textSearch = signal('');
  searchSubject = new Subject<string>();
  data: PagingData<Exam> = new PagingData<Exam>();

  constructor(
    private http: HttpClient,
    private router: Router,
    private bsModal: BsModalService,
    private toastr: ToastrService
  ) {
  }

  ngOnInit(): void {
    this.getListExam();
    this.searchSubject.pipe(
      debounceTime(500) // Wait for 500ms before calling API
    ).subscribe(() => this.getListExam());
  }

  createQuestion() {
    this.router.navigate([`/admin/exam/upsert`])
      .then();
  }

  getListExam(page: number = this.data.page, size: number = this.data.size) {
    this.http.get<ResponseData<PagingData<Exam>>>(`/api/test?page=${page}&size=${size}&key=${this.textSearch()}`)
      .subscribe(res => {
        if (res.success) {
          this.data = res.data;
        }
      });
  }

  onSearch() {
    this.searchSubject.next(this.textSearch());
  }

  refresh() {
    this.textSearch.set('');
    this.getListExam();
  }

  updateExam(exam: Exam) {
    const bsRef = this.bsModal.show(UpdateExamComponent, {
      class: 'modal-lg modal-dialog-centered',
      initialState: {
        params: signal(exam)
      }
    });
    bsRef?.content?.eventSubmit.subscribe(res => {
      if (res) {
        this.getListExam();
      }
    });
  }

  deleteExam(exam: Exam) {
    const bsRef = this.bsModal.show(ConfirmComponent, {
      class: 'modal-dialog-centered',
      initialState: {
        content: signal('Are you sure you want to delete this exam?'),
      }
    });
    bsRef?.content?.confirmed.subscribe((res: boolean) => {
      if (res) {
        this.http.delete<ResponseData<string>>(`/api/test?eid=${exam.testId}`)
          .subscribe(res => {
            if (res.success) {
              this.getListExam();
            }
          });
      }
    });
  }

  pageChanged(event: any) {
    this.getListExam(event.page, event.itemsPerPage);
  }

  detailExam(exam: Exam) {
    this.router.navigate([`/admin/exam/detail`], {
      queryParams: {
        eid: exam.testId
      }
    })
      .then();
  }

  importExcel() {
    const bsRef = this.bsModal.show(ImportExcelExamComponent, {
      class: 'modal-dialog-centered modal-lg'
    });
    bsRef?.content?.eventSubmit.subscribe(res => {
      if (res) {
        this.getListExam();
        this.toastr.success('Import successfully');
      } else {
        this.toastr.error('Import failed');
      }
    });
  }

  random() {
    const bsRef = this.bsModal.show(RandomTestComponent, {
      class: 'modal-dialog-centered modal-lg',
    });
    bsRef?.content?.eventSubmit.subscribe(res => {
      if (res) {
        this.getListExam();
        this.toastr.success('Random successfully');
      } else {
        this.toastr.error('Random failed');
      }
    });
  }
}
