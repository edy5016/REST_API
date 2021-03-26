package com.rest.api;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RestApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestApiApplication.class, args);
	}

	// 공용으로 쓸수 있는 부분이기 때문에 빈으로 등록후 사용.
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
