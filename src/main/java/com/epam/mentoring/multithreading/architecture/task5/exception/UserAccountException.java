package com.epam.mentoring.multithreading.architecture.task5.exception;

import lombok.Getter;

@Getter
public class UserAccountException extends RuntimeException {

    private final String errorCode;

    public UserAccountException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public UserAccountException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
