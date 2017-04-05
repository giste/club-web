package org.giste.club.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Main class of Club Web client.
 * 
 * @author Giste
 */
@SpringBootApplication
public class ClubWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClubWebApplication.class, args);
	}
	
	@Bean
	public RestTemplate restTemplate() {
	    return new RestTemplate();
	}
}
