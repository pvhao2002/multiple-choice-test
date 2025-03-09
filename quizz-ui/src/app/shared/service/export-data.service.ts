import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ResponseData} from '../model/response-data.model';
import {ToastrService} from 'ngx-toastr';

@Injectable({
  providedIn: 'root'
})
export class ExportDataService {
  exportType = new Map<string, { blobType: string, fileType: string }>([
    ['EXCEL', {
      blobType: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64,',
      fileType: '.xlsx',
    }],
    ['CSV', {blobType: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64,', fileType: '.csv'}],
    ['PDF', {blobType: 'application/pdf', fileType: '.pdf'}],
  ]);

  constructor(private http: HttpClient, private toast: ToastrService) {
  }

  exportData(url = '', request = {}, type = '', fileName = ''): (Observable<Blob | ResponseData<[]>>) {
    return new Observable<Blob | ResponseData<[]>>(observer => {
      this.http.post(url, request, {responseType: 'blob'}).subscribe({
        next: (res) => {
          const blob = new window.Blob([res], {type: (this.exportType.get(type)?.blobType || '')});
          const a = document.createElement('a');
          const blobURL = URL.createObjectURL(blob);
          a.download = fileName + (this.exportType.get(type)?.fileType || '');
          a.href = blobURL;
          document.body.appendChild(a);
          a.click();
          document.body.removeChild(a);
          observer.next(res);
        }, error: (err) => {
          this.toast.error(err);
          observer.next(err);
        }, complete: () => observer.complete(),
      });
    });
  }
}
