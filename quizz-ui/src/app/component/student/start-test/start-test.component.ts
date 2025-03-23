import {Component, OnDestroy, OnInit, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute, Router} from '@angular/router';
import {ResponseData} from '../../../shared/model/response-data.model';
import {ToastrService} from 'ngx-toastr';
import {DatePipe, DecimalPipe, NgClass, NgStyle} from '@angular/common';
import {BsModalService} from 'ngx-bootstrap/modal';
import {ConfirmV2Component} from '../../confirm-v2/confirm-v2.component';
import {FormsModule} from '@angular/forms';
import {ExamDetail, QuestionDetail} from '../../../shared/model/ExamDetail';
import {SubmitTest} from '../../../shared/model/SubmitTest';
import {fromEvent, interval, of, Subject, Subscription, switchMap, takeUntil, timer} from 'rxjs';
import {document} from 'ngx-bootstrap/utils';
import {tap} from 'rxjs/operators';
import {AuthenticationService} from '../../../shared/service/authentication.service';
import {ReviewResponse} from '../../../shared/model/ReviewResponse';

@Component({
  selector: 'app-start-test',
  imports: [
    NgClass,
    FormsModule,
    NgStyle,
    DatePipe,
    DecimalPipe
  ],
  templateUrl: './start-test.component.html',
  standalone: true,
  styleUrl: './start-test.component.scss'
})
export class StartTestComponent implements OnInit, OnDestroy {
  data: ExamDetail = new ExamDetail();
  dataReview: ReviewResponse = new ReviewResponse();
  isReview = signal(false);
  tabVisibilityDetector$: Subscription = new Subscription();
  mouseMoveDetector$: Subscription = new Subscription();
  mouseEnterSubject$ = new Subject<void>();
  originSubmitQuestion = new SubmitTest();
  intervalSyncAnswer = interval(2_000)
    .subscribe(() => {
      if (this.checkChangeAnswer()) {
        this.originSubmitQuestion = this.buildParam();
        this.syncAnswer(this.originSubmitQuestion);
      }
    });
  eid: number = 0;
  tid: number = 0;

  intervalDetectMultipleLogin = interval(2_000)
    .subscribe(() => {
      if (!this.isReview()) {
        this.http.post<ResponseData<boolean>>('api/users/multiple-login', {})
          .subscribe(res => {
            if (res.success && !res.data) {
              this.toast.error('Your account has been logged in from another location');
              this.authService.logout();
            }
          })
      }
    });
  minutes!: number;
  seconds!: number;
  timerSubscription!: Subscription;

  constructor(private http: HttpClient,
              private route: ActivatedRoute,
              private router: Router,
              private toast: ToastrService,
              private bsModal: BsModalService,
              private authService: AuthenticationService
  ) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(param => {
      this.eid = param['eid'];
      this.tid = param['tid'];
      this.isReview.set(param['review'] === 'true');
      if (!this.eid && !this.tid) {
        this.navigateToExam();
      } else {
        if (this.isReview()) {
          this.getTestReview();
        } else {
          this.getTestDetail(this.eid);
        }
      }
    });
  }

  getTestDetail(eid: number) {
    this.http.get<ResponseData<ExamDetail>>(`api/v2/test/detail?eid=${eid}`)
      .subscribe(res => {
        if (res.success) {
          this.data = res.data;

          if (this.data.questions.length < 0) {
            this.toast.error('This test has no question');
            this.navigateToExam();
            return;
          }

          if (!this.data.hasStarted) {
            this.initTestResult(eid);
          }
          if (this.data.hasMonitor) {
            this.detectTabVisibility();
            this.detectMouseMove();
          }
          this.originSubmitQuestion = this.buildParam();
          this.data.questions.forEach(q => q.isCheck = !!q.userAnswer);
          this.startCountdown();
        } else {
          this.toast.error(res.message);
          this.navigateToExam();
        }
      });
  }

  startCountdown() {
    let remainingTime = this.data.remainingTime; // Convert to seconds
    this.updateTime(remainingTime);

    this.timerSubscription = interval(1000).subscribe(() => {
      if (remainingTime > 0) {
        remainingTime--;
        this.updateTime(remainingTime);
      } else {
        this.toast.warning('Time is up');
        this.submitTest();
        this.timerSubscription.unsubscribe(); // Stop countdown at 0
      }
    });
  }

  updateTime(remainingTime: number) {
    this.minutes = Math.floor(remainingTime / 60);
    this.seconds = remainingTime % 60;
  }

  getTestReview() {
    this.http.get<ResponseData<ReviewResponse>>(`api/v2/test/review?tid=${this.tid}`)
      .subscribe(res => {
        if (res.success) {
          this.dataReview = res.data;
          this.dataReview.percentCorrect = Math.round(this.dataReview.totalCorrect / this.dataReview.totalQuestions * 100);
        } else {
          this.toast.error('This test is not available for review');
          this.navigateToExam();
        }
      });
  }

  submit() {
    const totalQuestionChecked = this.data.questions.filter(q => q.userAnswer).length;
    const message = this.isReview()
      ? 'Do you want to finish this review?'
      : `Do you want to submit this test? <br/>You have answered <b>${totalQuestionChecked}/${this.data.questions.length} </b> questions.`;
    const bsRef = this.bsModal.show(ConfirmV2Component, {
      class: 'modal-dialog-centered',
      initialState: {
        title: signal('Submit test'),
        message: signal(message),
      }
    });
    bsRef?.content?.onClick.subscribe((result: boolean) => {
      if (result) {
        this.submitTest();
      }
    });
  }

  submitTest() {
    const param = this.buildParam();
    this.http.post<ResponseData<string>>('api/v2/test/submit-test', param)
      .subscribe(res => {
        if (res.success) {
          this.toast.success('Submit test successfully');
          this.navigateToExam();
        } else {
          this.toast.error(res.message);
        }
      });
  }

  buildParam() {
    const param = new SubmitTest();
    param.testAttemptId = this.data.testAttemptId;
    param.numberWarning = this.data.numberTreating;
    param.examId = this.data.testId;
    param.answers = this.data.questions.map(q => {
      return {
        qid: q.questionId,
        answer: q.userAnswer
      };
    });
    return param;
  }

  onChooseQuestion(q: QuestionDetail) {
    q.isCheck = true;
  }

  detectTabVisibility() {
    this.tabVisibilityDetector$ = fromEvent(document, 'visibilitychange')
      .subscribe(() => {
        if (document.visibilityState === 'hidden') {
          this.toast.warning('You are not allowed to switch tabs during the test');
          this.data.numberTreating++;
        }
      });
  }

  detectMouseMove() {
    this.mouseMoveDetector$ = fromEvent(document, 'mouseleave')
      .pipe(
        switchMap(() => timer(2_000).pipe(
          takeUntil(fromEvent(document, 'mouseenter').pipe(
            tap(() => {
              this.mouseEnterSubject$.next(); // Thông báo khi chuột vào lại
            })
          )),
          tap(() => {
            this.toast.warning('You are not allowed to move the mouse out of the test area');
            this.data.numberTreating++;
          }),
          switchMap(() => of(null).pipe( // Chuyển sang observable không kết thúc
            takeUntil(this.mouseEnterSubject$) // Duy trì toast cho đến khi chuột vào lại
          ))
        ))
      )
      .subscribe();
  }

  checkChangeAnswer(): boolean {
    return (this.data.questions.some(q => {
        const qOrigin = this.originSubmitQuestion.answers.find(a => a.qid === q.questionId);
        return qOrigin ? q.userAnswer !== qOrigin.answer : !!q.userAnswer;
      }) && this.data.questions.some(q => q.userAnswer !== ''))
      || (this.data.hasMonitor && this.data.numberTreating !== this.originSubmitQuestion.numberWarning);
  }

  ngOnDestroy(): void {
    if (this.intervalSyncAnswer) {
      this.intervalSyncAnswer.unsubscribe();
    }
    if (this.intervalDetectMultipleLogin) {
      this.intervalDetectMultipleLogin.unsubscribe();
    }
    this.mouseMoveDetector$.unsubscribe();
    this.tabVisibilityDetector$.unsubscribe();
    this.mouseEnterSubject$.unsubscribe();
    if (this.timerSubscription) {
      this.timerSubscription.unsubscribe();
    }
  }

  navigateToExam() {
    this.router.navigate(['/student/course']).then();
  }

  initTestResult(eid: number) {
    this.http.post<ResponseData<number>>(`api/v2/test/start-test?eid=${eid}`, {})
      .subscribe(res => {
        if (res.success && res.data > 0) {
          this.data.testAttemptId = res.data;
        } else {
          this.toast.error('Server is error, please try again later');
          this.navigateToExam();
        }
      });
  }

  syncAnswer(params: SubmitTest) {
    this.http.post<ResponseData<string>>('api/v2/test/sync-answer', params)
      .subscribe(res => {
        if (!res.success) {
          this.toast.error(res.message);
        }
      });
  }

  getBgPercent(percent: number) {
    let color = 'red'; // Default: Danger

    if (percent >= 60) {
      color = 'green'; // Success
    } else if (percent >= 40) {
      color = 'orange'; // Warning
    }

    return {
      background: `radial-gradient(closest-side, white 79%, transparent 80% 100%),
                 conic-gradient(${color} ${percent}%, lightgray 0)`
    };
  }
}
