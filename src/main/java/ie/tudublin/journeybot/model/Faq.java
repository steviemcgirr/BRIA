package ie.tudublin.journeybot.model;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import static org.springframework.data.elasticsearch.annotations.FieldType.Keyword;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "faq", type = "trainFaq")
public class Faq {

	@Id
	String id;

	String question;

	String answer;

	private String[] tags;

}
