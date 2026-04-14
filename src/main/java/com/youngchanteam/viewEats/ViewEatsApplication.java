package com.youngchanteam.viewEats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ViewEatsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ViewEatsApplication.class, args);
	}

}
