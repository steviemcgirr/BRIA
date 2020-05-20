package ie.tudublin.journeybot.service;

import org.springframework.stereotype.Service;

import ie.tudublin.journeybot.model.JourneyFare;
import ie.tudublin.journeybot.model.Station;
import ie.tudublin.journeybot.repository.JourneyFareRepository;

@Service
public class JourneyFareService {

	public final JourneyFareRepository journeyFareRepository;

	public JourneyFareService(JourneyFareRepository journeyFareRepository) {
		this.journeyFareRepository = journeyFareRepository;
	}

	public JourneyFare getJourneyFare(Station origin, Station dest) {
		return journeyFareRepository.findByOriginStationAndDestStation(origin, dest);
	}

}
