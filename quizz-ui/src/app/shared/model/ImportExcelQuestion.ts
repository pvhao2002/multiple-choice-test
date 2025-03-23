export class ImportExcelQuestion {
  constructor(
    public name: string = '',
    public startDate: string = '',
    public endDate: string = '',
    public subjectId: number | null = null,
    public hasMonitor: boolean = false,
    public time: number = 0,
  ) {
  }
}
