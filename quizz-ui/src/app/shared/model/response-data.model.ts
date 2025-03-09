export interface ResponseData<T> {
  success: boolean;
  data: T,
  errorCode: string;
  message: string;
  status: number;
}

export class PagingData<T> {
  constructor(
    public contents: T[] = [],
    public page: number = 1,
    public size: number = 10,
    public totalRecords: number = 0
  ) {
  }
}
