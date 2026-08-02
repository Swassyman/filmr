package com.filmr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FilmrApplication {

	public static void main(String[] args) {
		SpringApplication.run(FilmrApplication.class, args);
	}
}
