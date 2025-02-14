export class User {
  constructor(
    public userId: number = 0,
    public email: string = '',
    public firstName: string = '',
    public lastName: string = '',
    public gender: string = '',
    public avatar: string = '',
    public status: string = '',
  ) {
  }
}
