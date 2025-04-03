import {Component, effect, inject, signal} from '@angular/core';
import {QuillEditorComponent} from 'ngx-quill';
import {FormsModule} from '@angular/forms';
import {PageTitleComponent} from '../../page-title/page-title.component';
import {Breadcumb} from '../../../shared/model/breadcumb';
import {rxResource} from '@angular/core/rxjs-interop';
import {HttpClient} from '@angular/common/http';
import {ResponseData} from '../../../shared/model/response-data.model';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-about-manage',
  imports: [
    QuillEditorComponent,
    FormsModule,
    PageTitleComponent,
  ],
  templateUrl: './about-manage.component.html',
  standalone: true,
  styleUrl: './about-manage.component.scss'
})
export class AboutManageComponent {

  breadCrumbs = [
    new Breadcumb('Home', '/'),
    new Breadcumb('Course Management', '/course'),
    new Breadcumb('About detail', '/about')
  ];
  http = inject(HttpClient);
  toast = inject(ToastrService);

}

export class About {
  constructor(
    public subjects: SubjectAboutDTO[] = [],
    public courses: CourseAboutDTO[] = [],
  ) {
  }
}

export class CourseAboutDTO {
  constructor(
    public courseCode: string = '',
    public startDate: string = '',
  ) {
  }
}

export class SubjectAboutDTO {
  constructor(
    public subjectName: string = '',
  ) {
  }
}
