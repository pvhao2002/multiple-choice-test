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
import {BsDatepickerModule} from 'ngx-bootstrap/datepicker';
import {DateService} from '../../../shared/service/date.service';

@Component({
  selector: 'app-exam-upsert',
  imports: [
    PageTitleComponent,
    FormsModule,
    NgSelectComponent,
    NgOptionComponent,
    BsDatepickerModule,
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
  startDate = new Date();
  endDate = new Date();
  subjects = signal<SubjectDTO[]>([])
  @Output() eventSubmit = new EventEmitter<boolean>();
  config = {
    withTimepicker: true,
    rangeInputFormat: 'DD/MM/YYYY HH:mm:ss',
    dateInputFormat: 'DD/MM/YYYY HH:mm:ss'
  };

  constructor(
    private http: HttpClient,
    private toast: ToastrService,
    protected fileService: TransferFileService,
    private dateService: DateService
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
    this.params().startDate = this.dateService.getFormatDate(this.startDate);
    this.params().endDate = this.dateService.getFormatDate(this.endDate);
    this.http.post<ResponseData<string>>('api/test', this.params())
      .subscribe(res => {
        if (res.success) {
          this.toast.success('Create exam success');

        } else {
          this.toast.error(res.message);
        }
      });
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
