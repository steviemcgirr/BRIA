package ie.tudublin.journeybot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ie.tudublin.journeybot.model.JourneyFare;
import ie.tudublin.journeybot.model.Station;

public interface JourneyFareRepository extends JpaRepository<JourneyFare, Integer>{

	JourneyFare findByOriginStationAndDestStation(Station origin, Station dest);
	
	

}
