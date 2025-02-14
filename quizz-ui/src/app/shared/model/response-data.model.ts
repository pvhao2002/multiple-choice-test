export interface ResponseData<T> {
  success: boolean;
  data: T,
  errorCode: string;
  message: string;
}

export class PagingData<T> {
  constructor(
    public contents: T[] = [],
    public page: number = 0,
    public size: number = 0,
    public totalRecords: number = 0
  ) {
  }
}
