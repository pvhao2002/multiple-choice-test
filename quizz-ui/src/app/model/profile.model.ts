export class ProfileDTO {
  constructor(
    public userId = -1,
    public email = '',
    public firstName = '',
    public lastName = '',
    public gender = '',
    public avatar = '',
    public role = '',
    public status = '',
  ) {
  }
}

export class UserPermission {
  constructor(
    public permissionName = '',
    public description = '',
  ) {
  }
}

export class PasswordDTO {
  constructor(
    public oldPassword = '',
    public newPassword = '',
    public confirmPassword = '',
  ) {
  }
}

