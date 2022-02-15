package com.example.circuitbreaker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SpringCloudCircuitbreakerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudCircuitbreakerApplication.class, args);
	}

}
