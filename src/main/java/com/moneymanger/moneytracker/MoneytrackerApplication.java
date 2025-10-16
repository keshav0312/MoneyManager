package com.moneymanger.moneytracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;

@EnableScheduling
@SpringBootApplication
@CrossOrigin(origins = "https://moneymanagerwebap.netlify.app")
public class MoneytrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoneytrackerApplication.class, args);
	}

}
