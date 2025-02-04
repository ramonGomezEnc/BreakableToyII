package com.flightsearch.backend;

import com.flightsearch.backend.flight.utils.DurationUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import com.flightsearch.backend.flight.utils.DurationUtils;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public DurationUtils durationUtils() {
		return new DurationUtils();
	}
}
