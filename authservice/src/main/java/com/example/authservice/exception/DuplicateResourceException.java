package com.example.authservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serializable;

@ResponseStatus(HttpStatus.CONFLICT)
@Getter
public class DuplicateResourceException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String resourceName;
    private final String fieldName;
    private final transient Object fieldValue; // Transient for serialization

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}
