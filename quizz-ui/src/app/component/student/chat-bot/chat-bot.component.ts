import {Component, OnInit, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {Breadcumb} from '../../../shared/model/breadcumb';
import {PageTitleComponent} from '../../page-title/page-title.component';
import {DatePipe, NgOptimizedImage} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {AskQuestion} from '../../../shared/model/AskQuestion';
import {UserService} from '../../../shared/service/user.service';
import {UserInfo} from '../../../shared/model/profile.model';
import {ResponseData} from '../../../shared/model/response-data.model';
import {MarkdownModule} from 'ngx-markdown';
import {Content, Part, PayloadGemini} from '../../../shared/model/PayloadGemini';

@Component({
  selector: 'app-chat-bot',
  imports: [
    PageTitleComponent,
    NgOptimizedImage,
    FormsModule,
    DatePipe,
    MarkdownModule
  ],
  templateUrl: './chat-bot.component.html',
  standalone: true,
  providers: [],
  styleUrl: './chat-bot.component.scss',
})
export class ChatBotComponent implements OnInit {
  breadCrumbs = [
    new Breadcumb('Home', '/'),
    new Breadcumb('Chat', '/student/chat-bot'),
  ];
  message = '';
  conversations: AskQuestion[] = [];
  user: UserInfo = new UserInfo();
  params: PayloadGemini = new PayloadGemini();
  isLoading = signal(false);

  constructor(private http: HttpClient,
              private toast: ToastrService,
              private userService: UserService
  ) {
  }

  ngOnInit(): void {
    this.user = JSON.parse(JSON.stringify(this.userService.profile));
    this.getData();
  }

  ask() {
    if (this.isLoading()) {
      return;
    }
    this.isLoading.set(true);
    if (!this.message) {
      this.toast.info('Please input message');
    }
    this.params.contents.push(new Content('user', [new Part(this.message)]));
    this.http.post<ResponseData<any>>('api/chatbot/ask', this.params)
      .subscribe(res => {
        this.isLoading.set(false);
        if (res.success) {
          const newMessage = new AskQuestion(0, this.user.studentId, this.message, res.data.response, res.data.time);
          this.conversations.push(newMessage);
          this.message = '';
        } else {
          this.toast.error(res.message);
        }
      });
  }

  getData() {
    this.http.get<ResponseData<ChatResponse>>('api/chatbot')
      .subscribe(res => {
        if (res.success) {
          this.conversations = res.data.histories;
          this.params = new PayloadGemini(res.data.contents);
          console.log(this.params)
        } else {
          this.toast.error(res.message);
        }
      });
  }
}


export class ChatResponse {
  constructor(
    public contents: Content[] = [],
    public histories: AskQuestion[] = []
  ) {
  }
}
