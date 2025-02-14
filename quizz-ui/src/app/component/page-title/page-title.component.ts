import {Component, input, OnInit} from '@angular/core';
import {NgClass} from '@angular/common';
import {Breadcumb} from '../../shared/model/breadcumb';
import {CONSTANT} from '../../shared/constant';

@Component({
  selector: 'app-page-title',
  imports: [
    NgClass
  ],
  templateUrl: './page-title.component.html',
  standalone: true,
  styleUrls: ['./page-title.component.scss', CONSTANT.lib1, CONSTANT.lib2]
})
export class PageTitleComponent implements OnInit{
  public data = input<Breadcumb[]>([]);

  title = '';

  ngOnInit(): void {
    this.title = this.data()[this.data().length - 1]?.label ?? '';
  }

}
