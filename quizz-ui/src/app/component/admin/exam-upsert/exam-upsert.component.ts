import {Component, EventEmitter, OnInit, Output, signal} from '@angular/core';
import {Breadcumb} from '../../../shared/model/breadcumb';
import {PageTitleComponent} from '../../page-title/page-title.component';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {AddExam, AddQuestion} from '../../../shared/model/add-exam';
import {FormsModule} from '@angular/forms';
import {TransferFileService} from '../../../shared/service/transfer-file.service';
import {PagingData, ResponseData} from '../../../shared/model/response-data.model';
import {SubjectDTO} from '../../../shared/model/subject';
import {NgOptionComponent, NgSelectComponent} from '@ng-select/ng-select';

@Component({
  selector: 'app-exam-upsert',
  imports: [
    PageTitleComponent,
    FormsModule,
    NgSelectComponent,
    NgOptionComponent
  ],
  templateUrl: './exam-upsert.component.html',
  standalone: true,
  styleUrl: './exam-upsert.component.scss',
  providers: [TransferFileService]
})
export class ExamUpsertComponent implements OnInit {
  breadCrumbs = [
    new Breadcumb('Home', '/'),
    new Breadcumb('Exam Management', '/admin/exam'),
    new Breadcumb('Create Exam', '/admin/exam/upsert')
  ];
  params = signal<AddExam>(new AddExam());
  subjects = signal<SubjectDTO[]>([])
  @Output() eventSubmit = new EventEmitter<boolean>();

  constructor(
    private http: HttpClient,
    private toast: ToastrService,
    protected fileService: TransferFileService
  ) {
  }

  removeQuestion(index: number) {
    this.params().listQuestion.splice(index, 1);
  }

  addQuestion() {
    this.params().listQuestion.push(new AddQuestion());
    this.params().numberOfQuestion = this.params().listQuestion.length;
  }

  submit() {
    this.http.post<ResponseData<string>>('api/test', this.params())
      .subscribe(res => {
        if (res.success) {
          this.toast.success('Create exam success');

        } else {
          this.toast.error(res.message);
        }
      })
  }

  processFile(event: any, idx: number) {
    this.fileService.processFile(event).subscribe({
      next: (res) => {
        this.params().listQuestion[idx].image = res;
      },
      error: (err) => {
        this.toast.error(err);
      }
    });
  }

  getListSubject() {
    this.http.get<ResponseData<PagingData<SubjectDTO>>>('api/subjects?page=1&size=1000')
      .subscribe(res => {
        if (res.success) {
          this.subjects.set(res.data.contents);
        }
      })
  }

  ngOnInit(): void {
    this.getListSubject();
  }

  reset() {
    this.params.set(new AddExam());
  }
}
