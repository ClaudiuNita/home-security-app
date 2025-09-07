package com.example.home_security_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HomeSecurityApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomeSecurityApiApplication.class, args);
	}

}
