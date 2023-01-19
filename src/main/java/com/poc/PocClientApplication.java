package com.poc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan({ "com.poc" })
@SpringBootApplication
@EnableScheduling
public class PocClientApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(PocClientApplication.class, args);
		
	}
}
