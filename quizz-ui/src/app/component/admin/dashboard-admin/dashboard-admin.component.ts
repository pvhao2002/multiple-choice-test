import { Component } from '@angular/core';
import {RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-dashboard-admin',
  imports: [
    RouterOutlet
  ],
  templateUrl: './dashboard-admin.component.html',
  standalone: true,
  styleUrl: './dashboard-admin.component.scss'
})
export class DashboardAdminComponent {

}
