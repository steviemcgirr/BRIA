package ie.tudublin.journeybot.dto.response;

import static org.springframework.data.elasticsearch.annotations.FieldType.Keyword;

import java.util.Set;

import org.springframework.data.elasticsearch.annotations.Field;

import ie.tudublin.journeybot.model.Faq;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FaqResponse {
	
	private String id;
	private String question;
	private String answer;
	@Field(type = Keyword)
    private String[] tags;
	
	public FaqResponse(Faq faq) {
		this.id = faq.getId();
		this.question = faq.getQuestion();
		this.answer = faq.getAnswer();
		this.tags = faq.getTags();
	}
	

}
