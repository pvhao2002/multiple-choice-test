export class RandomQuestion {
  constructor(
    public numberQuestion: number = 0,
    public name: string = '',
    public startDate: string = '',
    public endDate: string = '',
    public time: number = 0,
    public subjectId: number | null = null,
    public hasMonitor: boolean = false,
    public testIds: number[] = []
  ) {
  }
}
