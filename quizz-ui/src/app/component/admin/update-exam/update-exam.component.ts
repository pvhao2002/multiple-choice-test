import {Component, EventEmitter, OnInit, Output, signal} from '@angular/core';
import {Breadcumb} from '../../../shared/model/breadcumb';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {BsModalRef} from 'ngx-bootstrap/modal';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NgOptionComponent, NgSelectComponent} from '@ng-select/ng-select';
import {Exam} from '../../../shared/model/Exam';
import {SubjectDTO} from '../../../shared/model/subject';
import {PagingData, ResponseData} from '../../../shared/model/response-data.model';
import {BsDatepickerDirective, BsDatepickerInputDirective} from 'ngx-bootstrap/datepicker';
import {DateService} from '../../../shared/service/date.service';

@Component({
  selector: 'app-update-exam',
  imports: [
    ReactiveFormsModule,
    NgOptionComponent,
    NgSelectComponent,
    FormsModule,
    BsDatepickerDirective,
    BsDatepickerInputDirective
  ],
  templateUrl: './update-exam.component.html',
  standalone: true,
  styleUrl: './update-exam.component.scss'
})
export class UpdateExamComponent implements OnInit {
  breadCrumbs = [
    new Breadcumb('Home', '/'),
    new Breadcumb('Exam Management', '/admin/exam'),
    new Breadcumb('Create Exam', '/admin/exam/upsert')
  ];
  startDate = new Date();
  endDate = new Date();
  params = signal<Exam>(new Exam());
  subjects = signal<SubjectDTO[]>([])
  @Output() eventSubmit = new EventEmitter<boolean>();
  config = {
    withTimepicker: true,
    rangeInputFormat: 'DD/MM/YYYY h:mm',
    dateInputFormat: 'DD/MM/YYYY h:mm'
  };

  constructor(private http: HttpClient,
              private bsRef: BsModalRef,
              private toast: ToastrService,
              private dateService: DateService
  ) {
  }

  ngOnInit(): void {
    this.getListSubject();
  }

  submit() {
    this.params().startDate = this.dateService.getFormatDate(this.startDate);
    this.params().endDate = this.dateService.getFormatDate(this.endDate);
    this.http.patch<ResponseData<string>>('api/test', this.params())
      .subscribe(res => {
        if (res.success) {
          this.toast.success('Update exam success');
          this.eventSubmit.emit(true);
          this.bsRef.hide();
        } else {
          this.toast.error('Update exam fail');
        }
      });
  }

  cancel() {
    this.bsRef.hide();
  }

  getListSubject() {
    this.http.get<ResponseData<PagingData<SubjectDTO>>>('api/subjects?page=1&size=1000')
      .subscribe(res => {
        if (res.success) {
          this.subjects.set(res.data.contents);
        }
      })
  }
}
