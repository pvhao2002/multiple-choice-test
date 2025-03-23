import {Component, OnInit, signal} from '@angular/core';
import {PageTitleComponent} from '../../page-title/page-title.component';
import {Breadcumb} from '../../../shared/model/breadcumb';
import {debounceTime, Subject} from 'rxjs';
import {PagingData, ResponseData} from '../../../shared/model/response-data.model';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {BsModalService} from 'ngx-bootstrap/modal';
import {Course} from '../../../shared/model/Course';
import {FormsModule} from '@angular/forms';
import {PageChangedEvent, PaginationComponent} from 'ngx-bootstrap/pagination';
import {CourseUpsertComponent} from '../course-upsert/course-upsert.component';
import {ConfirmComponent} from '../../confirm/confirm.component';
import {ToastrService} from 'ngx-toastr';
import {AddStudentCourseComponent} from '../add-student-course/add-student-course.component';
import {AddExamCourseComponent} from '../add-exam-course/add-exam-course.component';

@Component({
  selector: 'app-course-list',
  imports: [
    PageTitleComponent,
    FormsModule,
    PaginationComponent
  ],
  templateUrl: './course-list.component.html',
  standalone: true,
  styleUrl: './course-list.component.scss'
})
export class CourseListComponent implements OnInit {
  breadCrumbs = [
    new Breadcumb('Home', '/'),
    new Breadcumb('Course Management', '/course'),
    new Breadcumb('List Course', '/course/list')
  ];
  textSearch = signal('');
  searchSubject = new Subject<string>();
  data: PagingData<Course> = new PagingData<Course>();

  constructor(
    private http: HttpClient
    , private router: Router
    , private bsModal: BsModalService
    , private toast: ToastrService
  ) {
    this.searchSubject.pipe(
      debounceTime(500) // Wait for 500ms before calling API
    ).subscribe(() => this.getListCourse());
  }

  ngOnInit(): void {
    this.getListCourse();
  }

  getListCourse(page: number = this.data.page, size: number = this.data.size) {
    this.http.get<ResponseData<PagingData<Course>>>(`api/courses?page=${page}&size=${size}&code=${this.textSearch()}`)
      .subscribe(res => {
        this.data = res.data;
      });
  }

  openCreate() {
    const ref = this.bsModal.show(CourseUpsertComponent, {
      class: 'modal-lg modal-dialog-centered draggable',
      initialState: {
        isPopupVisible: signal(true),
        title: signal('Create Course')
      }
    });

    ref.content?.eventOut.subscribe((res: boolean) => {
      if (res) {
        this.getListCourse();
      }
    });
  }

  onSearch() {
    this.searchSubject.next(this.textSearch());
  }

  update(item: Course) {
    const ref = this.bsModal.show(CourseUpsertComponent, {
      class: 'modal-lg modal-dialog-centered draggable',
      initialState: {
        isPopupVisible: signal(true),
        title: signal('Update Course'),
        courseSignal: signal(item),
        isAdd: signal(false)
      }
    });

    ref.content?.eventOut.subscribe((res: boolean) => {
      if (res) {
        this.getListCourse();
      }
    });
  }

  delete(item: Course) {
    this.bsModal.show(ConfirmComponent, {
      class: 'modal-dialog-centered',
    }).content?.confirmed.subscribe((res: boolean) => {
      if (res) {
        this.http.delete<ResponseData<string>>(`api/courses/${item.courseId}`)
          .subscribe(res => {
            if (res.success) {
              this.getListCourse();
              this.toast.success('Delete course success');
            } else {
              this.toast.error('Delete course failed');
            }
          });
      }
    });
  }

  pageChanged($event: PageChangedEvent) {
    this.getListCourse($event.page, $event.itemsPerPage);
  }

  addStudent(item: Course) {
    this.bsModal.show(AddStudentCourseComponent, {
      class: 'modal-xl modal-dialog-centered draggable',
      initialState: {
        courseId: signal(item.courseId)
      }
    }).content?.event.subscribe(res => {
      if (res) {
        this.getListCourse();
      }
    });
  }

  addExam(item: Course) {
    this.bsModal.show(AddExamCourseComponent, {
      class: 'modal-xl modal-dialog-centered draggable',
      initialState: {
        courseId: signal(item.courseId)
      }
    }).content?.event.subscribe(res => {
      if (res) {
        this.getListCourse();
      }
    });
  }

  export(item: Course) {
    window.open(`http://localhost:1122/api/courses/export?cid=${item.courseId}`, '_blank');
  }
}
