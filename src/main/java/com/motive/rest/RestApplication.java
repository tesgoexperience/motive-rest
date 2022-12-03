package com.motive.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Refactoring notes
 * Reliability
 * 		Thorough QA tests. Ensure 100% coverage for all service classes
 * 		Fix all TODOs, sonarqube 
 * Security
 * 		Use zuul to add request limit
 * 		Encode password
 * 		Add @Preauth authentication such as @PreAuth("motiveObject.getOwner().getUsername() == authenticatedUser.getUsername()")
 * 		Review Spring security standards : https://docs.spring.io/spring-security/reference/features/authentication/index.html and https://docs.spring.io/spring-security/reference/features/exploits/index.html
 */
@SpringBootApplication
public class RestApplication {
	public static void main(String[] args) {
		SpringApplication.run(RestApplication.class, args);
	}

}
