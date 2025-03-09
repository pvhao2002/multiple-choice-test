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
    public password = '',
  ) {
  }
}

export class UserInfo {
  constructor(
    public email = '',
    public firstName = '',
    public lastName = '',
    public gender = '',
    public avatar = '',
    public studentId = '',
    public label = '',
  ) {
    this.label = `${this.firstName} ${this.lastName}`;
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

