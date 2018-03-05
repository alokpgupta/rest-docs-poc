package io.gati.web.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.restdocs.constraints.ConstraintDescriptionResolver;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import io.gati.web.doc.CustomConstraintDescriptionResolver;
import io.gati.web.doc.OrderDocumentation;
import io.gati.web.mock.MockWebServerTracker;
import okhttp3.mockwebserver.MockWebServer;

@Configuration
@Profile("iso-test")
public class WebTestConfig {

	@Bean(initMethod = "start", destroyMethod = "shutdown")
	public MockWebServer mockWebServer() {
		return new MockWebServer();
	}
	
	@Bean
	public WebClient webClient(MockWebServer server) {
		return WebClient.create(server.url("/").toString());
	}
	
	@Bean
	public MockWebServerTracker mockWebServerTracker(MockWebServer server) {
		MockWebServerTracker mockWebServerTracker = new MockWebServerTracker();
		mockWebServerTracker.setServer(server);
		return mockWebServerTracker;
	}
	
	@Bean
	public ObjectMapper objectMapper(ObjectProvider<Jdk8Module> jdk8Module) {
		ObjectMapper mapper = new ObjectMapper();
		jdk8Module.ifAvailable(mapper::registerModule);
		return mapper;
	}
	
	@Bean
	public CustomConstraintDescriptionResolver constraintDescriptionResolver(MessageSource messageSource) {
		return new CustomConstraintDescriptionResolver(messageSource);
	}
	
	@Bean 
	public OrderDocumentation OrderDocumentation(ConstraintDescriptionResolver constraintDescriptionResolver) {
		OrderDocumentation orderDocumentation = new OrderDocumentation();
		orderDocumentation.setConstraintDescriptionResolver(constraintDescriptionResolver);
		return orderDocumentation;
	}
}
