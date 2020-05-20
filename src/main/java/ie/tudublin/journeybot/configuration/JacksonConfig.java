package ie.tudublin.journeybot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;


@Configuration
public class JacksonConfig {

	/**
	 * By providing an object mapper that registers the jackson Hibernate5Module
	 * we are no longer required to annotate every entity class with
	 * @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	 *
	 */
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Hibernate5Module());
		return mapper;
	}
	

}
