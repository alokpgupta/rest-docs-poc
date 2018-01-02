package io.gati.web;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class WebApplication {

	public static void main(String... args) {
		SpringApplicationBuilder builder = new SpringApplicationBuilder(WebApplication.class);
		builder.web(WebApplicationType.REACTIVE);
		builder.run(args);
	}
}
