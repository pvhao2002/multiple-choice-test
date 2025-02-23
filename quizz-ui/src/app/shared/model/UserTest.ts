export class UserTest {
  constructor(
    public email: string = '',
    public fullName: string = '',
    public subject: string = '',
    public testName: string = '',
    public hasMonitor: boolean = false,
    public totalQuestion: number = 0,
    public totalCorrect: number = 0,
    public status: string = '',
    public numberOfWarning: number = 0,
    public lastUpdate: string = ''
  ) {
  }
}
