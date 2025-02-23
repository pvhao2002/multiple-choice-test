import {Component, EventEmitter, OnInit, Output, signal} from '@angular/core';
import {ImportExcelQuestion} from '../../../../shared/model/ImportExcelQuestion';
import {HttpClient} from '@angular/common/http';
import {SubjectDTO} from '../../../../shared/model/subject';
import {PagingData, ResponseData} from '../../../../shared/model/response-data.model';
import {FormsModule} from '@angular/forms';
import {NgOptionComponent, NgSelectComponent} from '@ng-select/ng-select';
import {BsModalRef} from 'ngx-bootstrap/modal';

@Component({
  selector: 'app-import-excel-exam',
  imports: [
    FormsModule,
    NgSelectComponent,
    NgOptionComponent
  ],
  templateUrl: './import-excel-exam.component.html',
  standalone: true,
  styleUrl: './import-excel-exam.component.scss'
})
export class ImportExcelExamComponent implements OnInit {
  param: ImportExcelQuestion = new ImportExcelQuestion();
  subjects = signal<SubjectDTO[]>([]);
  @Output() eventSubmit = new EventEmitter<boolean>();
  formData: FormData = new FormData();

  constructor(private http: HttpClient,
              private bsRef: BsModalRef) {
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

  submit() {
    if (this.formData.has('payload')) {
      this.formData.delete('payload');
    }
    this.formData.append('payload', new Blob([JSON.stringify(this.param)], {type: 'application/json'}));
    this.http.post<ResponseData<string>>('api//test/import-excel', this.formData)
      .subscribe(res => {
        if (res.success) {
          this.eventSubmit.emit(true);
          this.bsRef.hide();
        } else {
          this.eventSubmit.emit(false);
        }
      });
  }

  processFile(event: any) {
    if (this.formData.has('file')) {
      this.formData.delete('file');
    }
    this.formData.append('file', event.target.files[0]);
  }
}
