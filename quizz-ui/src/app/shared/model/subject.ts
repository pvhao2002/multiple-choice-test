export class SubjectDTO {
  constructor(
    public subjectId: number = 0,
    public name: string = '',
    public icon: string = '',
    public count: number = 0,
    public lastUpdate: string = '',
  ) {
  }
}
