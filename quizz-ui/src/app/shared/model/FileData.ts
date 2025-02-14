export class FileData {
  constructor(
    public url: string | ArrayBuffer | null | undefined = '',
    public name: string = '',
    public type: string = '',
    public size: number = 0,
  ) {
  }
}
