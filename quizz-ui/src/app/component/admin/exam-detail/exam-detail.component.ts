import {Component, OnInit, signal} from '@angular/core';
import {Question} from '../../../shared/model/Exam';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {ResponseData} from '../../../shared/model/response-data.model';
import {PageTitleComponent} from '../../page-title/page-title.component';
import {Breadcumb} from '../../../shared/model/breadcumb';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CONSTANT} from '../../../shared/constant';
import {BsModalService} from 'ngx-bootstrap/modal';
import {QuestionUpsertComponent} from '../question-upsert/question-upsert.component';
import {AddQuestion} from '../../../shared/model/add-exam';
import {PopoverDirective} from 'ngx-bootstrap/popover';
import {ConfirmComponent} from '../../confirm/confirm.component';

@Component({
  selector: 'app-exam-detail',
  imports: [
    PageTitleComponent,
    ReactiveFormsModule,
    FormsModule,
    PopoverDirective
  ],
  templateUrl: './exam-detail.component.html',
  standalone: true,
  styleUrls: ['./exam-detail.component.scss', CONSTANT.lib2, CONSTANT.lib1]
})
export class ExamDetailComponent implements OnInit {
  breadCrumbs = [
    new Breadcumb('Home', '/'),
    new Breadcumb('Exam Management', '/admin/exam'),
    new Breadcumb('Exam Detail', '/admin/exam/detail')
  ];
  data: Question[] = [];
  examId: number | null = null;

  constructor(private route: ActivatedRoute,
              private http: HttpClient,
              private router: Router,
              private bsModal: BsModalService
  ) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(data => {
      this.examId = data['eid'];
      if (this.examId) {
        this.getExamDetail();
        this.breadCrumbs[this.breadCrumbs.length - 1].url = `/admin/exam/detail?eid=${this.examId}`;
      } else {
        // redirect to exam list
        this.router.navigate(['/admin/exam/list']).then();
      }
    });
  }

  getExamDetail() {
    this.http.get<ResponseData<Question[]>>(`/api/test/detail?eid=${this.examId}`)
      .subscribe(res => {
        if (res.success) {
          this.data = res.data;
        }
      });
  }

  refresh() {
    this.getExamDetail();
  }

  updateQuestion(question: Question) {
    const bsRef = this.bsModal.show(QuestionUpsertComponent, {
      class: 'modal-lg modal-dialog-centered',
      initialState: {
        question: signal(question),
        isAdd: signal(false)
      }
    });
    bsRef?.content?.eventSubmit.subscribe((res: boolean) => {
      if (res) {
        this.getExamDetail();
      }
    });
  }

  addQuestion() {
    const bsRef = this.bsModal.show(QuestionUpsertComponent, {
      class: 'modal-lg modal-dialog-centered',
      initialState: {
        question: signal(new AddQuestion()),
        examId: signal(this.examId),
        isAdd: signal(true)
      }
    });
    bsRef?.content?.eventSubmit.subscribe((res: boolean) => {
      if (res) {
        this.getExamDetail();
      }
    });

  }

  deleteQuestion(question: Question) {
    const bsRef = this.bsModal.show(ConfirmComponent, {
      class: 'modal-dialog-centered',
      initialState: {
        content: signal('Do you want to delete this question?')
      }
    });
    bsRef?.content?.confirmed.subscribe((res: boolean) => {
      if (res) {
        this.http.delete<ResponseData<string>>(`/api/questions?qid=${question.questionId}`)
          .subscribe(res => {
            if (res.success) {
              this.getExamDetail();
            }
          });
      }
    });
  }
}
