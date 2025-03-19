package com.example.paymentservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.paymentservice.dto.ErrorResponse;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {
    private ErrorResponse errorResponse;
    private LocalDateTime timestamp;

    @BeforeEach
    void setUp() {
        timestamp = LocalDateTime.now();
        errorResponse = new ErrorResponse(404, "Not Found", timestamp);
    }

    @Test
    void testGettersAndSetters() {
        ErrorResponse response = new ErrorResponse(500, "Internal Server Error", timestamp);
        
        assertEquals(500, response.getStatus());
        assertEquals("Internal Server Error", response.getMessage());
        assertEquals(timestamp, response.getTimestamp());
    }

    @Test
    void testToString() {
        String result = errorResponse.toString();
        
        assertTrue(result.contains("404"));
        assertTrue(result.contains("Not Found"));
        assertTrue(result.contains(timestamp.toString()));
    }

    @Test
    void testEqualsAndHashCode() {
        ErrorResponse response1 = new ErrorResponse(400, "Bad Request", timestamp);
        ErrorResponse response2 = new ErrorResponse(400, "Bad Request", timestamp);

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }
}
