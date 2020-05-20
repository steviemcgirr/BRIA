package ie.tudublin.journeybot.configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "elastic-search-config")
@EnableElasticsearchRepositories("ie.tudublin.journeybot.repository")
public class ElasticsearchConfig {
	
	
	private String ipAddress;
	private String clustername;
	private Integer port;
	private String index;
	private String type;
	private static final String CLUSTERNAME = "cluster.name";
	
	
	@Bean
	public Client client() throws UnknownHostException {
		Settings esSettings = Settings.builder().put(CLUSTERNAME, clustername).build();
		TransportClient transportClient = new PreBuiltTransportClient(esSettings);
		Client client = transportClient.addTransportAddresses(new TransportAddress(InetAddress.getByName(ipAddress), 9300));
		return client;
	}

	@Bean
	public ElasticsearchTemplate elasticsearchTemplate(Client client) {
		return new ElasticsearchTemplate(client);
	}

	
	
}
