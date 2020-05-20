package ie.tudublin.journeybot.controller;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.cloud.dialogflow.v2.QueryResult;

import ie.tudublin.journeybot.configuration.DialogFlowConfig;
import ie.tudublin.journeybot.dto.response.FaqResponse;
import ie.tudublin.journeybot.dto.response.JourneyInfoResponse;
import ie.tudublin.journeybot.dto.response.JourneyTimesResponse;
import ie.tudublin.journeybot.model.Faq;
import ie.tudublin.journeybot.model.JourneyStop;
import ie.tudublin.journeybot.service.DetectIntentTexts;
import ie.tudublin.journeybot.service.FAQService;
import ie.tudublin.journeybot.service.JourneyInfoService;
import ie.tudublin.journeybot.service.JourneyService;
import ie.tudublin.journeybot.service.JourneyTimeService;
import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
@RestController
@RequestMapping(JourneyTimeTableController.PATH)
public class JourneyTimeTableController {
	
	public static final String PATH = "/journeys";
	
	private static final String QUOTE = "\"";
	private static final String TERMS = "terms";
	private static final String PHRASES = "phrases";
	
	private final JourneyInfoService journeyInfoService;
	private final JourneyTimeService journeyTimeService;
    private final DialogFlowConfig dialogFlowConfig;
	private final DetectIntentTexts detectIntentTexts;
	private final JourneyService journeyService;
	private final FAQService faqService;
	
	
	
	
	@Autowired
	public JourneyTimeTableController(JourneyInfoService journeyInfoService, JourneyTimeService journeyTimeService, DialogFlowConfig dialogFlowConfig, 
	 DetectIntentTexts detectIntentTexts, JourneyService journeyService, FAQService faqService ) {
		this.journeyInfoService =  journeyInfoService;
		this.journeyTimeService = journeyTimeService;
		this.dialogFlowConfig = dialogFlowConfig;
		this.detectIntentTexts = detectIntentTexts;
		this.journeyService = journeyService;
		this.faqService =  faqService;
		
	}
	
	@GetMapping
	public List<JourneyInfoResponse> getJourneyInfo() {
		log.info("Retrieving all journeys");
		return journeyInfoService.getAllJourneyInfo();
	}
	
	@GetMapping("/faq")
	public String askFAQ() throws UnknownHostException {
		log.info("Retrieving all journeys");
		Pageable firstPageWithTwoElements = PageRequest.of(0, 5);
		String tag = "Are dogs allowed on the train?";
		return faqService.findByQuestion(tag);
	}
	
	
//	@GetMapping("/faq")
//	public String askFAQ(@RequestParam("query")String query) throws Exception{
//		
//		Map<String, List<String>> termsPhrases =  faqService.getListsOfTermAndPhrases(query);
//		
//		List<String> terms = termsPhrases.get(TERMS);
//		List<String> phrases = termsPhrases.get(PHRASES);
//		
//		for (String string : phrases) {
//			
//			System.out.println("these phrases: "+string);
//			
//		}
//		
//		for (String string : terms) {
//			
//			System.out.println("these terms: "+string);
//		}
//		
//		
//		
//	     return "hi";
//	}
	
	
	
	
	@GetMapping("/journeys")
	public String journeys() {
		
		List<JourneyStop> journeyStop = journeyService.findAll();
		
		for (JourneyStop js : journeyStop) {
			System.out.println("Hey there sexy " +js.getStop().getName());
			
		}
		
		return "hey";
	}
	
	@GetMapping("/test")
	public String testMagee() {
		log.info("Retrieving all journeys");
		return "sack";
	}
	
//	@GetMapping("/journey-times")
//	public List<JourneyTimesResponse> times(){
//		return journeyTimeService.findAllJourneyTimes();
//		
//		
//	}
	
	@GetMapping("/station")
	public String stations(){
		return journeyTimeService.getStationById();
		
		
	}
//	
//	@GetMapping("/journey-to-station")
//	public List<JourneyTimesResponse> journeyToStaion(){
//		return journeyTimeService.findJourneysToDestFromNow();
//		
//		
//	}
	
	@GetMapping("/station-name")
	public QueryResult stationName() throws Exception{
		String messsage =  "next journey to sligo";
		return DetectIntentTexts.detectIntentTexts(dialogFlowConfig.getProjectId(), messsage, 
				dialogFlowConfig.getSessionID(), dialogFlowConfig.getLanguageCode());
	}
	
//	@GetMapping("/stops")
//	public String stops(@RequestParam("id")Integer id) throws Exception{
//	     return journeyTimeService.getAllTimesAndStops(id);
//	}
	
//	@GetMapping("/fb-message")
//	public ObjectNode repsondToMessage(@RequestParam("message")String message) throws Exception{
//		return journeyTimeService.findJourneyToDestFromNow(message);
//	}
	

}
