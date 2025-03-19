package com.example.authservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
@Getter
public class InvalidTokenException extends RuntimeException {
    private final String errorCode;  // Made final

    public InvalidTokenException(String message) {
        super(message);
        this.errorCode = null;  // Explicitly set to null for clarity
    }

    public InvalidTokenException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
