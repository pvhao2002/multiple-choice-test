import {Component, OnInit} from '@angular/core';
import {ChartConfiguration, ChartData} from 'chart.js';
import {BaseChartDirective} from 'ng2-charts';
import {CHART_OPTIONS, CONSTANT} from '../../../shared/constant';
import {HttpClient} from '@angular/common/http';
import {PagingData, ResponseData} from '../../../shared/model/response-data.model';
import {ToastrService} from 'ngx-toastr';
import {Chart} from '../../../shared/model/Chart';
import {forkJoin} from 'rxjs';
import {Dashboard} from '../../../shared/model/Dashboard';
import {UserTest} from '../../../shared/model/UserTest';
import {FormsModule} from '@angular/forms';
import {PaginationComponent} from 'ngx-bootstrap/pagination';
import {DatePipe} from '@angular/common';
import {ExportDataService} from '../../../shared/service/export-data.service';

@Component({
  selector: 'app-home-admin',
  imports: [
    BaseChartDirective,
    FormsModule,
    PaginationComponent,
    DatePipe,
  ],
  templateUrl: './home-admin.component.html',
  standalone: true,
  styleUrls: ['./home-admin.component.scss', CONSTANT.lib2, CONSTANT.lib1]
})
export class HomeAdminComponent implements OnInit {
  public barChartOptions: ChartConfiguration<'bar'>['options'] = CHART_OPTIONS.bar;
  public barChartData: ChartData<'bar'> = CHART_OPTIONS.init;
  dashboard: Dashboard = new Dashboard();
  userTests: PagingData<UserTest> = new PagingData<UserTest>();

  constructor(private http: HttpClient,
              private toast: ToastrService,
              protected exportService: ExportDataService
  ) {
  }

  ngOnInit(): void {
    this.init();
  }

  init() {
    forkJoin([
      this.http.get<ResponseData<Chart>>('api/chart/total-test-by-exam'),
      this.http.get<ResponseData<Dashboard>>('api/home/admin'),
      this.http.get<ResponseData<PagingData<UserTest>>>('api/home/user-test')
    ]).subscribe(([res1, res2, res3]) => {
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
        this.dashboard = res2.data;
      } else {
        this.toast.error(res2.message);
      }

      if (res3.success) {
        this.userTests = res3.data;
      } else {
        this.toast.error(res3.message);
      }
    });
  }

  chartHovered(e: any): void {

  }

  chartClicked(e: any): void {

  }

  pageChanged(e: any) {
    this.http.get<ResponseData<PagingData<UserTest>>>(`api/home/user-test?page=${e.page}`)
      .subscribe(res3 => {
        if (res3.success) {
          this.userTests = res3.data;
        } else {
          this.toast.error(res3.message);
        }
      });
  }

  exportData() {
    window.open('http://localhost:1122/api/home/export', '_blank');
  }
}
