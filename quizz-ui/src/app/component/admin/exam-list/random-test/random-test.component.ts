import {Component, EventEmitter, OnInit, Output, signal} from '@angular/core';
import {NgOptionComponent, NgSelectComponent} from '@ng-select/ng-select';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {ImportExcelQuestion} from '../../../../shared/model/ImportExcelQuestion';
import {SubjectDTO} from '../../../../shared/model/subject';
import {HttpClient} from '@angular/common/http';
import {PagingData, ResponseData} from '../../../../shared/model/response-data.model';
import {BsModalRef} from 'ngx-bootstrap/modal';
import {Exam} from '../../../../shared/model/Exam';
import {forkJoin} from 'rxjs';
import {RandomQuestion} from '../../../../shared/model/RandomQuestion';
import {ToastrService} from 'ngx-toastr';
import {BsDatepickerDirective, BsDatepickerInputDirective} from 'ngx-bootstrap/datepicker';
import {DateService} from '../../../../shared/service/date.service';

@Component({
  selector: 'app-random-test',
  imports: [
    NgOptionComponent,
    NgSelectComponent,
    ReactiveFormsModule,
    FormsModule,
    BsDatepickerDirective,
    BsDatepickerInputDirective
  ],
  templateUrl: './random-test.component.html',
  standalone: true,
  styleUrl: './random-test.component.scss'
})
export class RandomTestComponent implements OnInit {
  param: RandomQuestion = new RandomQuestion();
  subjects = signal<SubjectDTO[]>([]);
  exams = signal<Exam[]>([]);
  @Output() eventSubmit = new EventEmitter<boolean>();
  startDate: Date = new Date();
  endDate: Date = new Date();
  config = {
    withTimepicker: true,
    rangeInputFormat: 'DD/MM/YYYY HH:mm:ss',
    dateInputFormat: 'DD/MM/YYYY HH:mm:ss'
  };

  constructor(private http: HttpClient,
              private bsRef: BsModalRef,
              private toast: ToastrService,
              private dateService: DateService
  ) {
  }

  ngOnInit(): void {
    this.getList();
  }

  getList() {
    forkJoin([
      this.http.get<ResponseData<PagingData<SubjectDTO>>>('api/subjects?page=1&size=1000'),
      this.http.get<ResponseData<PagingData<Exam>>>('api/test?page=1&size=1000')
    ]).subscribe(([subjectRes, examRes]) => {
      if (subjectRes.success) {
        this.subjects.set(subjectRes.data.contents);
      }

      if (examRes.success) {
        this.exams.set(examRes.data.contents);
      }
    });
  }


  submit() {
    if (this.param.numberQuestion > 100) {
      this.toast.error('Number of question must be less than 100');
      return;
    }
    this.param.startDate = this.dateService.getFormatDate(this.startDate);
    this.param.endDate = this.dateService.getFormatDate(this.endDate);

    this.http.post<ResponseData<any>>('api/test/random', this.param)
      .subscribe(res => {
        if (res.success) {
          this.eventSubmit.emit(true);
          this.bsRef.hide();
        } else {
          this.toast.error(res.message);
        }
      })
  }
}
