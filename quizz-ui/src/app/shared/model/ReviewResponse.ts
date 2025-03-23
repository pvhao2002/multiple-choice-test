export class ReviewResponse {
  constructor(
    public testId: number = 0,
    public hasMonitor: boolean = false,
    public name: string = '',
    public testAttemptId: number = 0,
    public numberOfWarning: number = 0,
    public totalCorrect: number = 0,
    public score: number = 0,
    public time: number = 0,
    public startTime: string = '',
    public endTime: string = '',
    public totalQuestions: number = 0,
    public percentCorrect: number = 0,
    public questions: ReviewQuestion[] = []
  ) {
  }
}

export class ReviewQuestion {
  constructor(
    public questionId: number = 0,
    public userAnswer: string = '',
    public isCorrect: boolean = false,
    public no: number = 0,
    public image: string = '',
    public content: string = '',
    public optionA: string = '',
    public optionB: string = '',
    public optionC: string = '',
    public optionD: string = '',
    public correctAnswer: string = ''
  ) {
  }
}
