package com.example.planmanagementservice;

import org.junit.jupiter.api.Test;

import com.example.planmanagementservice.dto.ErrorResponse;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void testErrorResponseConstructorAndGetters() {
        // Create a LocalDateTime object for the timestamp
        LocalDateTime timestamp = LocalDateTime.now();

        // Create an ErrorResponse instance using the constructor
        ErrorResponse errorResponse = new ErrorResponse(404, "Not Found", timestamp);

        // Verify the values assigned to the fields via the constructor
        assertEquals(404, errorResponse.getStatus(), "Status should be 404");
        assertEquals("Not Found", errorResponse.getMessage(), "Message should be 'Not Found'");
        assertEquals(timestamp, errorResponse.getTimestamp(), "Timestamp should match the provided value");
    }

    @Test
    void testSetters() {
        // Create a new ErrorResponse object
        ErrorResponse errorResponse = new ErrorResponse(0, null, null);

        // Set values using setters
        LocalDateTime timestamp = LocalDateTime.now();
        errorResponse.setStatus(500);
        errorResponse.setMessage("Internal Server Error");
        errorResponse.setTimestamp(timestamp);

        // Verify that the setters worked correctly
        assertEquals(500, errorResponse.getStatus(), "Status should be 500 after setter call");
        assertEquals("Internal Server Error", errorResponse.getMessage(), "Message should be 'Internal Server Error'");
        assertEquals(timestamp, errorResponse.getTimestamp(), "Timestamp should match the set value");
    }

    @Test
    void testToString() {
        // Create a LocalDateTime object for the timestamp
        LocalDateTime timestamp = LocalDateTime.now();

        // Create an ErrorResponse instance
        ErrorResponse errorResponse = new ErrorResponse(400, "Bad Request", timestamp);

        // Get the toString representation of the object
        String result = errorResponse.toString();

        // Check if the toString output contains relevant information
        assertTrue(result.contains("400"), "ToString should contain the status code.");
        assertTrue(result.contains("Bad Request"), "ToString should contain the message.");
        assertTrue(result.contains(timestamp.toString()), "ToString should contain the timestamp.");
    }

    @Test
    void testEqualsAndHashCode() {
        // Create two identical ErrorResponse instances
        LocalDateTime timestamp = LocalDateTime.now();
        ErrorResponse errorResponse1 = new ErrorResponse(404, "Not Found", timestamp);
        ErrorResponse errorResponse2 = new ErrorResponse(404, "Not Found", timestamp);

        // Test equality
        assertEquals(errorResponse1, errorResponse2, "ErrorResponse objects with the same data should be equal.");

        // Test hashCode
        assertEquals(errorResponse1.hashCode(), errorResponse2.hashCode(), "ErrorResponse objects with the same data should have the same hash code.");
    }
}
