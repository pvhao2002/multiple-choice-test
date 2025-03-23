import {Component, effect, inject, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {rxResource} from '@angular/core/rxjs-interop';
import {ResponseData} from '../../../shared/model/response-data.model';
import {About} from '../../admin/about-manage/about-manage.component';
import {PageTitleComponent} from '../../page-title/page-title.component';
import {Breadcumb} from '../../../shared/model/breadcumb';

@Component({
  selector: 'app-about',
  imports: [
    PageTitleComponent
  ],
  templateUrl: './about.component.html',
  standalone: true,
  styleUrl: './about.component.scss'
})
export class AboutComponent {
  breadCrumbs = [
    new Breadcumb('Home', '/'),
    new Breadcumb('About', '/student/student'),
  ];

  http = inject(HttpClient);
  toast = inject(ToastrService);

  about = rxResource({
    loader: (_) =>
      this.http.get<ResponseData<About>>(
        `/api/courses/about`
      ),
  });

  htmlText = signal<string>('');

  _ = effect(() => {
    const content = this.about.value()?.data?.content;
    if (content) {
      this.htmlText.set(content);
    }
  });
}
