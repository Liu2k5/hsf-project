package com.hsf.hsf_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HsfProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(HsfProjectApplication.class, args);
	}

}
