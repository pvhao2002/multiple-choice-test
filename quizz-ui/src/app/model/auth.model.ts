export interface LoginDTO {
  loginId: string;
  password: string;
}

export class LoginResponseDTO {
  constructor(
    public accessToken = '',
    public refreshToken = '',
    public isFirstLogin = false,
    public isPasswordExpired = false,
    public valid = false,
  ) {
  }
}
