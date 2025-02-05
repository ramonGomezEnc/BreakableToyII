package com.flightsearch.backend;

import com.flightsearch.backend.utils.DurationUtils;
import com.flightsearch.backend.utils.PaginationUtils;
import com.flightsearch.backend.utils.SortingUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

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

	@Bean
	public PaginationUtils paginationUtils() {
		return new PaginationUtils();
	}

	@Bean
	public SortingUtils sortingUtils() {
		return new SortingUtils();
	}
}
