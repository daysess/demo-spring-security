package com.mballem.curso.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mballem.curso.security.service.EmailService;

@SpringBootApplication
public class DemoSecurityApplication {

	public static void main(String[] args) {
		//System.out.println(new BCryptPasswordEncoder().encode("123"));
		SpringApplication.run(DemoSecurityApplication.class, args);
	}
	
	@Autowired
	EmailService service;	
}
