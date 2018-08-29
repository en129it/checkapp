package com.ddv.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication 
@EnableScheduling
public class Application {

	public static void main(String[] args) {
		if (args.length!=2) {
			System.out.println("Usage: Application <user name> <user password>");
		} else {
			SpringApplication.run(Application.class, args);
		}
	}
}
