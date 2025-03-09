export class AskQuestion {
  constructor(
    public id: number = 0,
    public studentId: string = '',
    public question: string = '',
    public answer: string = '',
    public createdAt: string = '',
  ) {
  }
}
