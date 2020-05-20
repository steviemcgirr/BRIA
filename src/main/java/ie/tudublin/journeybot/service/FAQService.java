package ie.tudublin.journeybot.service;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ie.tudublin.journeybot.configuration.ElasticsearchConfig;
import ie.tudublin.journeybot.dto.response.FaqResponse;
import ie.tudublin.journeybot.dto.response.JourneyTimesResponse;
import ie.tudublin.journeybot.model.Faq;
import ie.tudublin.journeybot.model.Time;
import ie.tudublin.journeybot.repository.FaqRepository;

;

@Service
public class FAQService {

	private ElasticsearchConfig elasticsearchConfig;
	private FaqRepository faqRepository;

	@Autowired
	public FAQService(ElasticsearchConfig elasticsearchConfig) {
		this.elasticsearchConfig = elasticsearchConfig;

	}
	public List<FaqResponse> findByFilteredTagQuery(String tag) {
		List<Faq> answers = faqRepository.findByFilteredTagQuery(tag);
		return answers.stream().map(answer -> buildFaqResponse(answer)).collect(Collectors.toList());

	}
	public String findByQuestion(String question) throws UnknownHostException {
		String include[] = { "answer", "question", "tags" };
		String exclude[] = { "" };
		float minScore = 0.5f;
		String answer = "";
		List<String> nouns = new ArrayList<>();
	
		SearchResponse response = elasticsearchConfig.client().prepareSearch(elasticsearchConfig.getIndex()).setTypes(elasticsearchConfig.getType())
				.setSearchType(SearchType.DEFAULT).setMinScore(minScore).setQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("question", question))
						.should(QueryBuilders.termQuery("tags", nouns)))
				.setFetchSource(include, exclude).setFrom(0).setSize(60).setExplain(true).execute().actionGet();
		SearchHit[] results = response.getHits().getHits();
	
		
		if (results.length > 0) {
			String sourceDoc = results[0].getSourceAsString();
			JSONObject json = new JSONObject(sourceDoc);
			System.out.println(json);
			answer = json.get("answer").toString();
		}
		

		if (answer.equals("") || answer == null) {
			System.out.println("ElasticSearch Found no answer");
			return null;
		}

		return answer;	
		
	}

	public FaqResponse buildFaqResponse(Faq faq) {
		return new FaqResponse(faq);
	}
}
