import {Component, OnInit} from '@angular/core';
import {BaseChartDirective} from 'ng2-charts';
import {ChartConfiguration, ChartData} from 'chart.js';
import {CHART_OPTIONS} from '../../../shared/constant';
import {HttpClient} from '@angular/common/http';
import {PagingData, ResponseData} from '../../../shared/model/response-data.model';
import {Chart} from '../../../shared/model/Chart';
import {ToastrService} from 'ngx-toastr';
import {MyTestWeek} from '../../../shared/model/MyTestWeek';
import {forkJoin} from 'rxjs';
import {DatePipe} from '@angular/common';
import {PaginationComponent} from 'ngx-bootstrap/pagination';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-home',
  imports: [
    BaseChartDirective,
    DatePipe,
    PaginationComponent,
    FormsModule
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
  standalone: true
})
export class HomeComponent implements OnInit {
  public barChartOptions: ChartConfiguration<'bar'>['options'] = CHART_OPTIONS.bar;
  public barChartData: ChartData<'bar'> = CHART_OPTIONS.init;
  public data: PagingData<MyTestWeek> = new PagingData();

  constructor(private http: HttpClient,
              private toast: ToastrService) {
  }

  ngOnInit(): void {
    this.init();
  }

  init() {
    forkJoin([
      this.http.get<ResponseData<Chart>>('api/chart/my-test-week'),
      this.http.get<ResponseData<PagingData<MyTestWeek>>>('api/home/student')
    ]).subscribe(([res1, res2]) => {
      if (res1.success) {
        const datasets = [...res1.data.datasets];
        this.barChartData = {
          labels: [...res1.data.labels],
          datasets: datasets
        }
      } else {
        this.toast.error(res1.message);
      }

      if (res2.success) {
        this.data = res2.data;
      } else {
        this.toast.error(res2.message);
      }
    });
  }

  pageChanged(e: any) {
    this.http.get<ResponseData<PagingData<MyTestWeek>>>(`api/home/student?page=${e.page}`)
      .subscribe(res => {
        if (res.success) {
          this.data = res.data;
        } else {
          this.toast.error(res.message);
        }
      });
  }

}
