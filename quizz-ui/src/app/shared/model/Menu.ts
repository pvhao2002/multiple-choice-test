export class Menu {
  constructor(
    public name: string = '',
    public icon: string = '',
    public path: string = '',
    public children: ChildMenu[] = [],
    public active: boolean = false,
    public key: string = '',
  ) {
    this.key = '#' + this.name.toLowerCase().replace(/ /g, '_');
    this.icon = 'mdi ' + this.icon;
  }
}

export class ChildMenu {
  constructor(
    public name: string = '',
    public path: string = '',
    public active: boolean = false,
  ) {
  }
}
