import {Component, EventEmitter, Output, signal} from '@angular/core';
import {BsModalRef} from 'ngx-bootstrap/modal';

@Component({
  selector: 'app-confirm',
  imports: [],
  templateUrl: './confirm.component.html',
  standalone: true,
  styleUrl: './confirm.component.scss'
})
export class ConfirmComponent {
  @Output() confirmed = new EventEmitter<boolean>();
  content = signal('Do you want to Continue?');

  constructor(private bsRef: BsModalRef) {
  }

  click(value: boolean) {
    this.confirmed.emit(value);
    this.bsRef.hide();
  }
}
