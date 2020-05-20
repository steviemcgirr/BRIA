package ie.tudublin.journeybot.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ie.tudublin.journeybot.model.JourneyStop;
import ie.tudublin.journeybot.repository.JourneyRepository;

@Service
public class JourneyService {
	
	JourneyRepository journeyRepository;
	
	public JourneyService(JourneyRepository journeyRepository) {
		this.journeyRepository = journeyRepository;
	}
	
	
	public List<JourneyStop> findAll(){
		System.out.println("le bag");
		return journeyRepository.findAll();
		
	}


	public Optional<JourneyStop> findByIdJourneys(int i) {
		// TODO Auto-generated method stub
		return null;
	}

}
