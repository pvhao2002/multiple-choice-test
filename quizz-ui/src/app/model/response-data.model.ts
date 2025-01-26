interface ResponseErrorData {
  code: string;
  message: string;
}

export interface ResponseData<T> {
  success: boolean;
  data: T,
  error: ResponseErrorData
}

export class ResponseDataPaging<T> {
  constructor(
    public clazz : new() => T,
    public pageNo  = 0,
    public pageSize = 0,
    public totalRows = 0,
    public totalPages = 0,
    public items: T[] = [],
  ) {
    this.items = this.items.map(a => new clazz(...(a as [])));
  }
}

export class ResponseDataWithoutPaging<T> {
  constructor(public clazz: new () => T, public items: T[] = []) {
    this.items = this.items.map((a) => new clazz(...(a as [])));
  }
}
