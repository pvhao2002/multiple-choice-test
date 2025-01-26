export interface BasePagingModel {
  pageNo: number;
  pageSize: number;
  totalPages: number;
  totalRows: number;
}

export interface CustomPagingModel extends BasePagingModel {
  previousText?: string;
  nextText?: string;
  firstText?: string;
  lastText?: string;
}

export interface ResponseWithPaging<T> extends BasePagingModel {
  data: T;
}
