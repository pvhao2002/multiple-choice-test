import {Injectable} from '@angular/core';
import {FileData} from '../model/FileData';
import {Observable} from 'rxjs';

@Injectable()
export class TransferFileService {
  fileData: any;
  mFile: FileData = new FileData();

  handleFileInput(event: any) {
    const file = event.target.files[0];
    this.handleFiles(file);
  }

  processFile(event: any) {
    return new Observable<any>((obs) => {
      const file = event.target.files[0];
      if (!file) {
        obs.error('No file selected');
        return;
      }

      const reader = new FileReader();
      reader.onload = (e) => {
        const base64 = e.target?.result;
        this.fileData = file;
        obs.next(base64);
        obs.complete();
      };
      reader.onerror = (error) => obs.error(error);
      reader.readAsDataURL(file);
    });
  }


  handleFiles(file: any) {
    if (file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        this.mFile.url = e.target?.result;
        this.fileData = file;
        this.mFile.name = file.name;
        this.mFile.type = file.type;
        // convert file size to KB
        this.mFile.size = Math.round(file.size / 1024);
      };

      if (file.type.startsWith('image/')) {
        reader.readAsDataURL(file);
      } else {
        reader.readAsText(file);
      }
    }
  }

  allowDrop(event: any) {
    event.preventDefault();
  }

  handleDrop(event: any) {
    event.preventDefault();
    const files = event.dataTransfer.files[0];
    this.handleFiles(files);
  }

  reset() {
    this.fileData = undefined;
    this.mFile = new FileData();
  }
}
