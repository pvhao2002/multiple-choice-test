import {Component, EventEmitter, OnInit, Output, signal} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AddQuestion} from '../../../shared/model/add-exam';
import {HttpClient} from '@angular/common/http';
import {BsModalRef} from 'ngx-bootstrap/modal';
import {TransferFileService} from '../../../shared/service/transfer-file.service';
import {ResponseData} from '../../../shared/model/response-data.model';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-question-upsert',
  imports: [
    ReactiveFormsModule,
    FormsModule
  ],
  templateUrl: './question-upsert.component.html',
  standalone: true,
  styleUrl: './question-upsert.component.scss',
  providers: [TransferFileService]
})
export class QuestionUpsertComponent implements OnInit {
  question = signal<AddQuestion>(new AddQuestion())
  isAdd = signal(true);
  examId = signal<number | null>(null);
  @Output() eventSubmit = new EventEmitter<boolean>();

  constructor(private http: HttpClient,
              private bsRef: BsModalRef,
              private fileService: TransferFileService,
              private toast: ToastrService
  ) {
  }

  ngOnInit(): void {
    console.log(this.question());
  }

  processFile(event: any) {
    this.fileService.processFile(event).subscribe(url => {
      if (url) {
        this.question().image = url;
      }
    });
  }

  submit() {
    if (this.isAdd()) {
      this.create();
    } else {
      this.update();
    }
  }

  create() {
    if (!this.examId()) {
      this.toast.error('Exam id is required');
      this.bsRef.hide();
      return;
    }
    const formData = this.buildFormData(this.examId());
    this.http.post<ResponseData<string>>('api/questions', formData)
      .subscribe(res => {
        if (res.success) {
          this.toast.success('Create question success');
          this.eventSubmit.emit(true);
          this.bsRef.hide();
        } else {
          this.toast.error('Create question failed');
        }
      });
  }

  update() {
    const formData = this.buildFormData();
    this.http.patch<ResponseData<string>>('api/questions', formData)
      .subscribe(res => {
        if (res.success) {
          this.toast.success('Create question success');
          this.eventSubmit.emit(true);
          this.bsRef.hide();
        } else {
          this.toast.error('Create question failed');
        }
      });
  }

  buildFormData(eid: number | null = null) {
    if (eid) {
      this.question().examId = eid;
    }
    const formData = new FormData();
    formData.append('question', new Blob([JSON.stringify(this.question())], {type: 'application/json'}));
    if (this.fileService.fileData) {
      formData.append('file', this.fileService.fileData);
    }
    return formData;
  }

  cancel() {
    this.bsRef.hide();
  }
}
