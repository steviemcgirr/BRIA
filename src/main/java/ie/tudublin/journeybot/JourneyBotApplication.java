package ie.tudublin.journeybot;

import java.io.FileInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Component;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.collect.Lists;

import ie.tudublin.journeybot.configuration.ElasticsearchConfig;
import ie.tudublin.journeybot.configuration.YAMLConfig;
import ie.tudublin.journeybot.service.FAQService;

@SpringBootApplication(scanBasePackages = { "me.ramswaroop.jbot", "ie.tudublin.journeybot" })
@Component
public class JourneyBotApplication implements CommandLineRunner {

	@Autowired
	private YAMLConfig yamlConfig;

	public static void main(String[] args) {
		SpringApplication.run(JourneyBotApplication.class, args);

		System.out.println("arse");

	}

	public void run(String... args) throws Exception {
		System.out.println("port: " + yamlConfig.getPort());
		System.out.println("app name: " + yamlConfig.getName());

		// Otherwise credentials are read from the GOOGLE_APPLICATION_CREDENTIALS
		// environment variable.
		GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(
				"/Users/stevenmcgirr/eclipse-workspace/journey-bot/src/main/resources/journey-bot-ybelxe-466d29a8515f.json"));
		Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

//        System.out.println("Buckets:");
//        Page<Bucket> buckets = storage.list();
//        for (Bucket bucket : buckets.iterateAll()) {
//          System.out.println(bucket.toString());
//        
//
//      
//     
//    }

	}

	private final FAQService faqService;
	private final ElasticsearchConfig elasticsearchConfig;

	@Autowired
	public JourneyBotApplication(FAQService faqService, ElasticsearchConfig elasticsearchConfig) {
		this.faqService = faqService;
		this.elasticsearchConfig = elasticsearchConfig;
	}
}
