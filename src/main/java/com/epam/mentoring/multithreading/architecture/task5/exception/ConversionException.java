package com.epam.mentoring.multithreading.architecture.task5.exception;

public class ConversionException extends RuntimeException {

    private final String errorCode;

    public ConversionException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ConversionException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

}
