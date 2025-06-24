package ru.skypro.hogwarts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition
public class HogwartsApplication {

	public static void main(String[] args) {
		SpringApplication.run(HogwartsApplication.class, args);
	}

}
