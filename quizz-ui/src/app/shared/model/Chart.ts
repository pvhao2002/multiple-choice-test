export class Chart {
  constructor(
    public labels: any[] = [],
    public datasets: Dataset[] = []
  ) {
  }
}

export class Dataset {
  constructor(
    public data: any[] = [],
    public label: string = ''
  ) {
  }
}
