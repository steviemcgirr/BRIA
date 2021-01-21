package ie.tudublin.journeybot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	
		public static final String SWAGGER_BASE_APPLICATION_PACKAGE = "ie.tudublin.journeybot.controller";

		@Bean
		public Docket api() {
			return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.basePackage(SWAGGER_BASE_APPLICATION_PACKAGE)).paths(PathSelectors.any()).build()
					.pathMapping("/todo");
		}


}


