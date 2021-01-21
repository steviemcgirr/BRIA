package ie.tudublin.journeybot.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@PropertySource("classpath:application.yml")
@ConfigurationProperties
public class DialogFlowConfig {
	
	private String projectId;
	private String sessionID;
	private String languageCode;
}
