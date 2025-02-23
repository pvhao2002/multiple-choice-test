import {Component, EventEmitter, Output, signal} from '@angular/core';
import {BsModalRef} from 'ngx-bootstrap/modal';

@Component({
  selector: 'app-confirm-v2',
  imports: [],
  templateUrl: './confirm-v2.component.html',
  standalone: true,
  styleUrl: './confirm-v2.component.scss'
})
export class ConfirmV2Component {
  title = signal('');
  message = signal('');
  @Output() onClick = new EventEmitter<boolean>();

  constructor(
    private bsRef: BsModalRef
  ) {
  }

  click(isConfirm: boolean) {
    this.onClick.emit(isConfirm);
    this.bsRef.hide();
  }
}
