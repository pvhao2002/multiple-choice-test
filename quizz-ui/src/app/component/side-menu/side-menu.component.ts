import {Component, OnInit} from '@angular/core';
import {NgClass, NgTemplateOutlet} from '@angular/common';
import {AuthenticationService} from '../../shared/service/authentication.service';
import {ChildMenu, Menu} from '../../shared/model/Menu';
import {CONSTANT} from '../../shared/constant';
import {Router, RouterLink, RouterLinkActive} from '@angular/router';

@Component({
  selector: 'app-side-menu',
  imports: [
    NgClass,
    NgTemplateOutlet,
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './side-menu.component.html',
  standalone: true,
  styleUrls: ['./side-menu.component.scss', CONSTANT.lib1, CONSTANT.lib2]
})
export class SideMenuComponent implements OnInit {
  menuStudent: Menu[] = [
    new Menu('Home', 'mdi-home-outline', '/student/about'),
    new Menu('Exam', 'mdi-school-outline', '/student/exam', [
      new ChildMenu('Dashboard', '/student/home'),
      new ChildMenu('List Course', '/student/course'),
    ]),
    new Menu('Learning Result', 'mdi-book-education-outline', '/student/result'),
    new Menu('Chat bot', 'mdi-chat-processing-outline', '/student/chat-bot'),
  ]

  menuAdmin: Menu[] = [
    new Menu('Dashboard', 'mdi-home-outline', '/admin/home'),
    new Menu('Course Management', 'mdi-book-outline', '/admin/course', [
      new ChildMenu('List', '/admin/course/list'),
      new ChildMenu('Create', '/admin/course/upsert'),
    ]),
    new Menu('User Management', 'mdi-account-group-outline', '/admin/user-management'),
    new Menu('Subject Management', 'mdi-book-open-page-variant-outline', '/admin/subject', [
      new ChildMenu('List', '/admin/subject/list'),
      new ChildMenu('Create', '/admin/subject/upsert'),
    ]),
    new Menu('Exam Management', 'mdi-school-outline', '/admin/exam', [
      new ChildMenu('List', '/admin/exam/list'),
      new ChildMenu('Create', '/admin/exam/upsert'),
    ]),
  ];

  currentMenu: Menu[] = [];

  constructor(
    protected authenService: AuthenticationService
    , private router: Router
  ) {
  }

  expandMenu(menu: Menu) {
    menu.active = !menu.active;
  }

  directTo(menu: Menu) {
    this.router.navigate([menu.path]).then(r => r);
  }

  ngOnInit(): void {
    this.currentMenu = this.authenService.isAdmin ? [...this.menuAdmin] : [...this.menuStudent];
  }
}
