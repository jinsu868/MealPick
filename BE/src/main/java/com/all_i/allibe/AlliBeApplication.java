package com.all_i.allibe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class AlliBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlliBeApplication.class, args);
	}

}
