import {CONSTANT} from '../constant';

export class AddExam {
  constructor(
    public examName: string = '',
    public hasMonitor: boolean = false,
    public numberOfQuestion: number = CONSTANT.defaultNumberQuestion,
    public startDate: string = '',
    public endDate: string = '',
    public time: number = 0,
    public listQuestion: AddQuestion[] = [],
    public subjectId: number | null = null,
  ) {
    for (let i = 0; i < CONSTANT.defaultNumberQuestion; i++) {
      this.listQuestion.push(new AddQuestion());
    }
  }
}

export class AddQuestion {
  constructor(
    public questionId: number | null = null,
    public examId: number | null = null,
    public no: number | null = null,
    public image: any = null,
    public content: string = '',
    public optionA: string = '',
    public optionB: string = '',
    public optionC: string = '',
    public optionD: string = '',
    public answer: string = '',
  ) {
  }
}
