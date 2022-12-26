package com.springboot.security.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    DUPLICATED_USER_NAME(HttpStatus.CONFLICT, "Username is duplicated"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Cannot find Username"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "Unmatched Password");

    //DUPLICATED_USER_NAME 작성 시 필요 (변수 지정)
    private HttpStatus status;
    private String message;
}
