package ie.tudublin.journeybot.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ie.tudublin.journeybot.model.Fare;
import ie.tudublin.journeybot.model.JourneyFare;
import ie.tudublin.journeybot.repository.FareRepository;

@Service
public class FareService {
	
	
	private final FareRepository fareRepository;
	
	@Autowired
	public FareService(FareRepository faiRepository) {
		this.fareRepository = faiRepository;
	}
    
	public Optional<Fare> findById(Integer id) {
		return fareRepository.findById(id);
	}
		
		

}
