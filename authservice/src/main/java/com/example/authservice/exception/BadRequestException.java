package com.example.authservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serializable;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Getter
public class BadRequestException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String errorCode;
    private final transient Object[] params; // Making it transient for serialization

    public BadRequestException(String message) {
        super(message);
        this.errorCode = null;
        this.params = null;
    }

    public BadRequestException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.params = null;
    }

    public BadRequestException(String message, String errorCode, Object[] params) {
        super(message);
        this.errorCode = errorCode;
        this.params = params;
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
        this.params = null;
    }
}
