import {Component, effect, inject, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {rxResource} from '@angular/core/rxjs-interop';
import {ResponseData} from '../../../shared/model/response-data.model';
import {PageTitleComponent} from '../../page-title/page-title.component';
import {TranslateModule} from '@ngx-translate/core';
import {NgOptimizedImage, SlicePipe} from '@angular/common';

@Component({
  selector: 'app-about',
  imports: [
    PageTitleComponent,
    TranslateModule,
    NgOptimizedImage,
    SlicePipe
  ],
  templateUrl: './about.component.html',
  standalone: true,
  styleUrl: './about.component.scss'
})
export class AboutComponent {
  http = inject(HttpClient);
  toast = inject(ToastrService);

  about = rxResource({
    loader: (_) =>
      this.http.get<ResponseData<About>>(
        `/api/home/about`
      ),
  });

  aboutData = signal<About>(new About());

  _ = effect(() => {
    const data = this.about.value()?.data;
    if (data) {
      this.aboutData.set(data);
    }
  });

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
