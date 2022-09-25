package com.gabs.minhasfinancias;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class MinhasfinanciasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MinhasfinanciasApplication.class, args);
	}

}
