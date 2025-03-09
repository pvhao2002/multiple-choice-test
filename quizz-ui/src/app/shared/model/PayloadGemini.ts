export class PayloadGemini {
  constructor(
    public contents: Content[] = [],
  ) {
  }
}


export class Content {
  constructor(
    public role: string = '',
    public parts: Part[] = [],
  ) {
  }
}


export class Part {
  constructor(
    public text: string = ''
  ) {
  }
}
