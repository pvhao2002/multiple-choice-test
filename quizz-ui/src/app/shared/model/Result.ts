import {PagingData} from './response-data.model';

export class Result {
  constructor(
    public testAttemptId: number = 0,
    public testId: number = 0,
    public name: string = '',
    public hasMonitor: number = 0,
    public score: number = 0,
    public totalCorrect: number = 0,
    public status: string = '',
    public totalQuestions: number = 0,
    public numberWarning: number = 0,
    public startTime: string = '',
    public finishTime: string = '',
  ) {
  }
}

export class ResponseResult {
  constructor(
    public complete: PagingData<Result> = new PagingData<Result>(),
    public incomplete: PagingData<Result> = new PagingData<Result>()
  ) {
  }
}
