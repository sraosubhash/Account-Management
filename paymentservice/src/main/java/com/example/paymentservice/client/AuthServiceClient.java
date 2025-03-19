package com.example.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {
    @GetMapping("/account/validate-user/{userId}")
    ResponseEntity<Boolean> validateUser(@PathVariable Long userId);

    @GetMapping("/account/validate-user/{userId}/{role}")
    ResponseEntity<Boolean> validateRole(@PathVariable Long userId,@PathVariable String role);
}
