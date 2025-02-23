export class ImportExcelQuestion {
  constructor(
    public name: string = '',
    public subjectId: number | null = null,
    public hasMonitor: boolean = false
  ) {
  }
}
