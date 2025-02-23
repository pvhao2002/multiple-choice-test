package com.app.quizzservice.model.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum ErrorCodeEnum {

    SUCCESS(200, "Successful"),

    BAD_REQUEST(400, "Bad request"),
    NO_QUESTION(400, "No question found"),
    UN_AUTHORIZATION(401, "Authorization required"),
    DENIED_IP(403, "IP not allow to be connected"),
    PERMISSION_DENIED(403, "You do not have permission to access this api"),
    INVALID_INPUT(403, "Invalid input"),
    TOO_MANY_REQUEST(403, "Too many requests"),
    ACCOUNT_LOCKED(403, "Account locked"),
    ACCOUNT_INACTIVE(403, "Account need to be activated"),
    REGISTER_FAILED(403, "Register failed"),
    ACCOUNT_NOT_FOUND(403, "Account not found"),
    PASSWORD_INCORRECT(403, "Password incorrect"),
    SERVER_ERROR(500, "There was an error in the services. Please contact the Help Desk"),
    ;

    private final int code;
    private final String message;

    ErrorCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @JsonProperty("success")
    public boolean success() {
        return getCode() == ErrorCodeEnum.SUCCESS.getCode();
    }
}
