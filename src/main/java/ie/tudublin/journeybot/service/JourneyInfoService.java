package ie.tudublin.journeybot.service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import antlr.collections.List;
import ie.tudublin.journeybot.dto.response.JourneyInfoResponse;
import ie.tudublin.journeybot.model.Station;
import ie.tudublin.journeybot.model.Transport;
import ie.tudublin.journeybot.repository.JourneyInfoRepository;



@Service
public class JourneyInfoService {
	
	private final JourneyInfoRepository JourneyInfoRepository;
	
	@Autowired
	public JourneyInfoService(JourneyInfoRepository JourneyInfoRepository) {
		this.JourneyInfoRepository = JourneyInfoRepository;
	}
	
	public java.util.List<Transport> findAllJourneys(){
		return JourneyInfoRepository.findAll();
	}
	
	public java.util.List<JourneyInfoResponse> getAllJourneyInfo(){
		
		
		
		java.util.List<Transport> Journeys = findAllJourneys();
		
		
		
		return Journeys.stream().map(Transport -> buildJourneyInfoResponse(Transport)).collect(Collectors.toList());
	}
	
	public JourneyInfoResponse buildJourneyInfoResponse(Transport Transport) {
		return new JourneyInfoResponse(Transport);
		
	}
	
	public Optional<Transport> findById(Integer id) {
		return JourneyInfoRepository.findById(id);
	}

}
