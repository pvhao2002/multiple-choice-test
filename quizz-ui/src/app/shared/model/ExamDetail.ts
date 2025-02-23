export class ExamDetail {
  constructor(
    public testId: number = 0,
    public name: string = '',
    public totalQuestion: number = 0,
    public hasMonitor: boolean = false,
    public hasStarted: boolean = false,
    public numberTreating: number = 0,
    public testAttemptId: number = 0,
    public questions: QuestionDetail[] = [],
  ) {
  }
}


export class QuestionDetail {
  constructor(
    public questionId: number = 0,
    public no: number = 0,
    public image: string = '',
    public content: string = '',
    public optionA: string = '',
    public optionB: string = '',
    public optionC: string = '',
    public optionD: string = '',
    public answer: string = '',
    public userAnswer: string = '',
    public isCheck: boolean = false,
  ) {
  }
}
