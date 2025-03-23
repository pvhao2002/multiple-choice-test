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

  onContentChanged(event: any) {
    this.htmlText.set(event.html);
  }

  submit() {
    this.http.post<ResponseData<string>>('api/courses/about', new About(1, this.htmlText()))
      .subscribe(res => {
        if (res.success) {
          this.toast.success('Update about success');
        } else {
          this.toast.error('Update about failed');
        }
      });
  }
}

export class About {
  constructor(
    public aboutId: number,
    public content: string
  ) {
  }
}
