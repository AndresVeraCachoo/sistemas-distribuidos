package com.practica2.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory; // IMPORT NATIVO DE SPRING
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class FrontendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FrontendApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		// Configuramos la fábrica de conexiones con 5000 milisegundos (5 segundos)
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(5000);
		factory.setReadTimeout(5000);

		return new RestTemplate(factory);
	}
}