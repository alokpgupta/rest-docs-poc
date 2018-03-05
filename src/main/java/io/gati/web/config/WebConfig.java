package io.gati.web.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

@Configuration
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {

	@Bean
	public WebClient webClient() {
		return WebClient.create("http://remotehost:80");
	}

	@Bean(name = "messageSource")
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource bean = new ReloadableResourceBundleMessageSource();
		bean.setBasename("classpath:messages/localization");
		bean.setDefaultEncoding("UTF-8");
		return bean;
	}

	@Bean(name = "validator")
	public LocalValidatorFactoryBean validator() {
		LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
		bean.setValidationMessageSource(messageSource());
		return bean;
	}

	@Override
	public Validator getValidator() {
		return validator();
	}
	
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/**")
                    .addResourceLocations("/static", "classpath:/static/");
    }
    
    @Bean
    public Jdk8Module jdk8Module() {
    		Jdk8Module jdk8Module = new Jdk8Module();
    		jdk8Module.configureAbsentsAsNulls(true);
    		return jdk8Module;
    }
}
