package com.example.planmanagementservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.planmanagementservice.dto.ApiResponse;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {
    private ApiResponse<String> successResponse;
    private ApiResponse<String> errorResponse;

    @BeforeEach
    void setUp() {
        successResponse = ApiResponse.success("Data loaded successfully.");
        errorResponse = ApiResponse.error("Something went wrong.");
    }

    @Test
    void testSuccessResponseConstructor() {
        assertTrue(successResponse.isSuccess(), "Response should be successful.");
        assertEquals("Success", successResponse.getMessage(), "Success message should be 'Success'");
        assertEquals("Data loaded successfully.", successResponse.getData(), "Response data should match the input data.");
    }

    @Test
    void testErrorResponseConstructor() {
        assertFalse(errorResponse.isSuccess(), "Response should not be successful.");
        assertEquals("Something went wrong.", errorResponse.getMessage(), "Error message should match the input message.");
        assertNull(errorResponse.getData(), "Data should be null for error response.");
    }

    @Test
    void testSuccessWithCustomMessageAndData() {
        ApiResponse<String> customSuccessResponse = ApiResponse.success("Operation successful", "Custom data");
        assertTrue(customSuccessResponse.isSuccess(), "Response should be successful.");
        assertEquals("Operation successful", customSuccessResponse.getMessage(), "Custom message should be set correctly.");
        assertEquals("Custom data", customSuccessResponse.getData(), "Custom data should be returned.");
    }

    @Test
    void testErrorWithMessage() {
        ApiResponse<String> customErrorResponse = ApiResponse.error("An error occurred");
        assertFalse(customErrorResponse.isSuccess(), "Response should not be successful.");
        assertEquals("An error occurred", customErrorResponse.getMessage(), "Error message should be set correctly.");
        assertNull(customErrorResponse.getData(), "Data should be null for error response.");
    }

    @Test
    void testSettersAndGetters() {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setMessage("Data fetched");
        apiResponse.setData("Some data");

        assertTrue(apiResponse.isSuccess(), "Getter for success should return true.");
        assertEquals("Data fetched", apiResponse.getMessage(), "Getter for message should return the correct message.");
        assertEquals("Some data", apiResponse.getData(), "Getter for data should return the correct data.");
    }
}
