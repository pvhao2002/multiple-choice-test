export class SubmitTest {
  constructor(
    public testAttemptId: number = 0,
    public numberWarning: number = 0,
    public examId: number = 0,
    public answers: Answer[] = [],
  ) {
  }
}

export class Answer {
  constructor(
    public qid: number = 0,
    public answer: string = '',
  ) {
  }
}
