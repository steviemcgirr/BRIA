package ie.tudublin.journeybot.controller;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.elasticsearch.common.inject.PrivateBinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.ui.context.Theme;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.protobuf.ListValue;
import com.google.cloud.dialogflow.v2.Intent.Message.QuickReplies;

import ie.tudublin.journeybot.configuration.DialogFlowConfig;
import ie.tudublin.journeybot.dto.response.JourneyInfoResponse;
import ie.tudublin.journeybot.dto.response.JourneyTimesResponse;
import ie.tudublin.journeybot.service.DetectIntentTexts;
import ie.tudublin.journeybot.service.FAQService;
import ie.tudublin.journeybot.service.JourneyInfoService;
import ie.tudublin.journeybot.service.JourneyTimeService;
import me.ramswaroop.jbot.core.common.Controller;
import me.ramswaroop.jbot.core.common.EventType;
import me.ramswaroop.jbot.core.common.JBot;
import me.ramswaroop.jbot.core.facebook.Bot;
import me.ramswaroop.jbot.core.facebook.FbService;
import me.ramswaroop.jbot.core.facebook.models.Attachment;
import me.ramswaroop.jbot.core.facebook.models.Button;
import me.ramswaroop.jbot.core.facebook.models.Element;
import me.ramswaroop.jbot.core.facebook.models.Event;
import me.ramswaroop.jbot.core.facebook.models.Message;
import me.ramswaroop.jbot.core.facebook.models.Payload;
import me.ramswaroop.jbot.core.facebook.models.User;

@JBot
@Profile("facebook")
public class FbBot extends Bot {

	@Value("${fbBotToken}")
	private String fbToken;

	@Value("${fbPageAccessToken}")
	private String pageAccessToken;

	@Override
	public String getFbToken() {
		return fbToken;
	}

	@Override
	public String getPageAccessToken() {
		return pageAccessToken;
	}

	private static final String TRANSPORT_RUNNING = "transportRunning";
	private static final String MESSAGE = "message";
	private static final String JOURNEY_ID_KEY = "journeyId";
	private static final String FROM_STATION_KEY = "fromStation";
	private static final String TO_STATION_KEY = "toStation";
	private static final String FAQ_INTENT = "faq.intent";
	private static final String TO_DESTINATION_INTENT = "to.destination.intent";
	private static final String DEFAULT_FALLBACK_INTENT = "Default Fallback Intent";
	private static final String TRANSPORT_TYPE = "transportType";
	private static final String BUS = "bus";
	private static final String TRAIN = "train";

	private static Integer JOURNEY_ID;
	private static Integer fromStation;
	private static Integer toStation;
	private static String userMessage;
	private static QueryResult queryResult;
	private static ObjectNode objectNode;

	private final FbService fbService;
	private final FAQService faqService;
	private final JourneyInfoService journeyInfoService;
	private final JourneyTimeService journeyTimeService;
	private final DialogFlowConfig dialogFlowConfig;
	private final DetectIntentTexts detectIntentTexts;

	@Autowired
	public FbBot(JourneyInfoService journeyInfoService, JourneyTimeService journeyTimeService, FbService fbService,
			FAQService faqService, DialogFlowConfig dialogFlowConfig, DetectIntentTexts detectIntentTexts) {
		this.journeyInfoService = journeyInfoService;
		this.journeyTimeService = journeyTimeService;
		this.fbService = fbService;
		this.faqService = faqService;
		this.dialogFlowConfig = dialogFlowConfig;
		this.detectIntentTexts = detectIntentTexts;
	}

	@PostConstruct
	public void init() {
		setGetStartedButton("hi");
		setGreetingText(new Payload[] { new Payload().setLocale("default").setText("Bus and Rail Information Assitant."
				+ "To beging click the " + "\"Get Started\" button or just type \"Hi\".") });
	}

	@Controller(events = { EventType.MESSAGE, EventType.POSTBACK }, pattern = "^(?i)(hi|hello|hey)$")
	public void onGetStarted(Event event) {

		User user = fbService.getUser(event.getSender().getId().toString(), pageAccessToken);
		Button[] quickReplies = new Button[] {
				new Button().setContentType("text").setTitle("Services Available").setPayload("services available"), };
		reply(event, new Message().setText("Hi " + user.getFirstName()
				+ ", I am BRIA. Ask me a question or click \"Services Available\" to see how I can assist your journey needs")
				.setQuickReplies(quickReplies));
	}

	@Controller(events = { EventType.MESSAGE, EventType.POSTBACK }, next = "whereFrom")
	public void onReceiveDM(Event event) throws Exception {

		queryResult = DetectIntentTexts.detectIntentTexts(dialogFlowConfig.getProjectId(), event.getMessage().getText(),
				dialogFlowConfig.getSessionID(), dialogFlowConfig.getLanguageCode());

		if (queryResult.getIntent().getDisplayName().equals(FAQ_INTENT)) {
			String reply = faqService.findByQuestion(event.getMessage().getText());
			Button[] quickReplies = new Button[] {
					new Button().setContentType("text").setTitle("YES").setPayload("more info"),
					new Button().setContentType("text").setTitle("NO").setPayload("later"), };
			reply(event, new Message().setText(reply + " \nWas this the information you were looking for? ")
					.setQuickReplies(quickReplies));
		}

		if (queryResult.getIntent().getDisplayName().equals(TO_DESTINATION_INTENT)) {
			startConversation(event, "whereFrom");
			reply(event, "Where are you departing from? Just tell me the town or station name.");

		}
		if (queryResult.getIntent().getDisplayName().equals(DEFAULT_FALLBACK_INTENT)) {
			queryResult.getFulfillmentText();
			reply(event, queryResult.getFulfillmentText());
		}

	}

	@Controller
	public void whereFrom(Event event) throws Exception {
		QueryResult departStation = DetectIntentTexts.detectIntentTexts(dialogFlowConfig.getProjectId(),
				event.getMessage().getText(), dialogFlowConfig.getSessionID(), dialogFlowConfig.getLanguageCode());
		stopConversation(event);
		System.out.println("this departing station: "
				+ departStation.getParameters().getFieldsOrThrow("stations").getStringValue());
		objectNode = journeyTimeService.findJourneyTimes(queryResult,
				departStation.getParameters().getFieldsOrThrow("stations").getStringValue());
		stopConversation(event);
		if (objectNode.get(TRANSPORT_TYPE).asText().equals(TRAIN)) {
			if (objectNode.get(TRANSPORT_RUNNING).asBoolean(true)) {
				Button[] quickReplies = new Button[] {
						new Button().setContentType("text").setTitle("MORE INFO").setPayload("more info"),
						new Button().setContentType("text").setTitle("LATER TRAINS").setPayload("later journeys"), };
				JOURNEY_ID = objectNode.get(JOURNEY_ID_KEY).intValue();
				fromStation = objectNode.get(FROM_STATION_KEY).intValue();
				toStation = objectNode.get(TO_STATION_KEY).intValue();
				reply(event, new Message().setText(objectNode.get(MESSAGE).asText()).setQuickReplies(quickReplies));
			} else {
				Button[] quickReplies = new Button[] {
						new Button().setContentType("text").setTitle("TRAINS TOMORROW").setPayload("times tomorrow"),
						new Button().setContentType("text").setTitle("SEE BUSES").setPayload("see buses"), };
				reply(event, new Message().setText(objectNode.get(MESSAGE).asText()).setQuickReplies(quickReplies));
			}
		} else {
			if (objectNode.get(TRANSPORT_RUNNING).asBoolean(true)) {
				Button[] quickReplies = new Button[] {
						new Button().setContentType("text").setTitle("MORE INFO").setPayload("more info"),
						new Button().setContentType("text").setTitle("LATER BUSES").setPayload("later times"), };
				JOURNEY_ID = objectNode.get(JOURNEY_ID_KEY).intValue();
				fromStation = objectNode.get(FROM_STATION_KEY).intValue();
				toStation = objectNode.get(TO_STATION_KEY).intValue();
				reply(event, new Message().setText(objectNode.get(MESSAGE).asText()).setQuickReplies(quickReplies));
			} else {
				Button[] quickReplies = new Button[] {
						new Button().setContentType("text").setTitle("BUSES TOMORROW").setPayload("times tomorrow"),

				};
				reply(event, new Message().setText(objectNode.get(MESSAGE).asText()).setQuickReplies(quickReplies));
			}
		}

	}

	@Controller(events = EventType.QUICK_REPLY, pattern = "(later times)")
	public void laterTimes(Event event) {
		String reply = journeyTimeService.laterJourneyTimes(objectNode);
		reply(event, reply);

	}

	@Controller(events = EventType.QUICK_REPLY, pattern = "times tomorrow")
	public void timesTomorrow(Event event) {
		String reply = journeyTimeService.getJourneyTimesTomorrow(objectNode);
		reply(event, reply);
	}

	@Controller(events = EventType.QUICK_REPLY, pattern = "services available")
	public void onReceiveQuickReply(Event event) {
		reply(event, "I can provide time table information and scheduling for Irish rail and Bus Éireann services.\n"
				+ "I am also here to help with any questions you may have relating to these services.");

	}

	@Controller(events = EventType.QUICK_REPLY, pattern = "(more info)")
	public void onReceiveMoreInfo(Event event) {

		Button[] quickReplies = new Button[] { new Button().setContentType("text").setTitle("YES").setPayload("yes"),
				new Button().setContentType("text").setTitle("NO").setPayload("no"), };

		String message = journeyTimeService.getAllTimesAndStops(JOURNEY_ID, fromStation, toStation);
		reply(event, new Message().setText(message).setQuickReplies(quickReplies));
	}
}
