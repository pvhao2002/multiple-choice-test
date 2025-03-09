import {Component, EventEmitter, OnInit, Output, signal} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {PageTitleComponent} from '../../page-title/page-title.component';
import {Breadcumb} from '../../../shared/model/breadcumb';
import {Course} from '../../../shared/model/Course';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {ResponseData} from '../../../shared/model/response-data.model';
import {BsModalRef} from 'ngx-bootstrap/modal';
import {NgClass} from '@angular/common';

@Component({
  selector: 'app-course-upsert',
  imports: [
    FormsModule,
    PageTitleComponent,
    NgClass
  ],
  templateUrl: './course-upsert.component.html',
  standalone: true,
  styleUrl: './course-upsert.component.scss'
})
export class CourseUpsertComponent implements OnInit {
  isAdd = signal(true);
  title = signal('');
  isPopupVisible = signal(false);
  breadCrumbs = [
    new Breadcumb('Home', '/'),
    new Breadcumb('Course Management', '/admin/course'),
    new Breadcumb('Create Course', '/admin/course/upsert')
  ];

  courseSignal = signal(new Course());
  @Output() eventOut = new EventEmitter<boolean>();

  constructor(private http: HttpClient,
              private toast: ToastrService,
              private bsRef: BsModalRef
  ) {
  }

  ngOnInit(): void {
  }

  submit() {
    if (!this.courseSignal().courseCode) {
      this.toast.error('Course code is required');
      return;
    }
    if (this.isAdd()) {
      this.create();
    } else {
      this.update();
    }
  }

  create() {
    this.http.post<ResponseData<string>>('api/courses', this.courseSignal())
      .subscribe(res => {
        if (res.success) {
          this.toast.success('Create course success');
          this.eventOut.emit(true);
          if (this.isPopupVisible()) {
            this.bsRef.hide();
          } else {
            this.courseSignal.set(new Course());
          }
        } else {
          this.toast.error('Create course failed');
        }
      });
  }

  update() {
    this.http.patch<ResponseData<string>>('api/courses', this.courseSignal())
      .subscribe(res => {
        if (res.success) {
          this.toast.success('Update course success');
          this.eventOut.emit(true);
          this.bsRef.hide();
        } else {
          this.toast.error('Update course failed');
        }
      });
  }

  closePopup() {
    this.bsRef.hide();
  }
}
