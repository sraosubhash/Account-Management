package com.example.planmanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.example")
@EnableDiscoveryClient
@EnableFeignClients
public class PlanmanagementserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlanmanagementserviceApplication.class, args);
	}

}
