package com.garf.garfpay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GarfpayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GarfpayApplication.class, args);
	}

}
