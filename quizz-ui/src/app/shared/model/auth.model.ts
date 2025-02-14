export class LoginDTO {
  constructor(
    public email: string = '',
    public password: string = '',
    public captcha: string = '',
  ) {
  }
}

export class LoginResponseDTO {
  constructor(
    public accessToken = '',
    public refreshToken = '',
    public isAdmin = false,
    public valid = false,
  ) {
  }
}
