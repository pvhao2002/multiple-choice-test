export class Exam {
  constructor(
    public testId: number = 0,
    public name: string = '',
    public hasMonitor: boolean = false,
    public totalQuestions: number = 0,
    public startDate: string = '',
    public endDate: string = '',
    public listQuestion: Question[] = [],
    public close: boolean = false,
    public subjectId: number | null = null,
    public count: number = 0,
    public updatedAt: string = '',
  ) {
  }
}

export class Question {
  constructor(
    public questionId: number = 0,
    public examId: number | null = null,
    public no: number = 0,
    public hasImage: boolean = false,
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
