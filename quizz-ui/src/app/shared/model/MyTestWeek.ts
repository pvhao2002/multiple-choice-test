export class MyTestWeek {
  constructor(
    public subject: string = '',
    public exam: string = '',
    public hasMonitor: boolean = false,
    public totalQuestion: number = 0,
    public totalAnswered: number = 0,
    public totalWarning: number = 0,
    public lastUpdate: string = ''
  ) {
  }
}
