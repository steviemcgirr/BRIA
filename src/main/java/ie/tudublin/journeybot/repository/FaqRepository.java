package ie.tudublin.journeybot.repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import ie.tudublin.journeybot.model.Faq;

@Repository
public interface FaqRepository extends ElasticsearchRepository<Faq, String> {

	List<Faq> findByQuestion(String quesion);

	@Query("{\"bool\": {\"must\": {\"match_all\": {}}, \"filter\": {\"term\": {\"tags\": \"?0\" }}}}")
	List<Faq> findByFilteredTagQuery(String tag);

}
