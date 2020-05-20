package ie.tudublin.journeybot.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.cloud.dialogflow.v2.TextInput.Builder;
import com.google.common.collect.Maps;
import com.google.protobuf.Descriptors.FieldDescriptor;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * DialogFlow API Detect Intent sample with text inputs.
 */
@Service
public class DetectIntentTexts {
 public static QueryResult detectIntentTexts(String projectId, String messgae, String sessionId,  String languageCode) throws Exception {
      
	  Map<String, QueryResult> queryResults = Maps.newHashMap();
	  
	  
    // Instantiates a client
    try (SessionsClient sessionsClient = SessionsClient.create()) {
      // Set the session name using the sessionId (UUID) and projectID (my-project-id)
      SessionName session = SessionName.of(projectId, sessionId);
      System.out.println("Session Path: " + session.toString());

      // Detect intents for each text input
     
        // Set the text (hello) and language code (en-US) for the query
        Builder textInput = TextInput.newBuilder().setText(messgae).setLanguageCode(languageCode);

        // Build the query with the TextInput
        QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

        // Performs the detect intent request
        DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);

        // Display the query result
        QueryResult queryResult = response.getQueryResult();

        System.out.println("====================");
        System.out.format("Query Text: '%s'\n", queryResult.getQueryText());
        System.out.format("Detected Intent: %s (confidence: %f)\n",
            queryResult.getIntent().getDisplayName(), queryResult.getIntentDetectionConfidence());
        System.out.format("Fulfillment Text: '%s'\n", queryResult.getFulfillmentText());
        System.out.format("Parameters Text: '%s'\n", queryResult.getParameters());
        
//        System.out.format("Destination: '%s'\n", queryResult.getParameters().containsFields("stations"));
//        
//        
//        System.out.println(queryResult.getParameters().getFieldsOrThrow("stations"));
//        
        

        return queryResult;
      }
    
    
  }
  // [END dialogflow_detect_intent_text]
}
