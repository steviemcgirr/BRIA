package ie.tudublin.journeybot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ie.tudublin.journeybot.model.JourneyStop;

public interface JourneyRepository extends JpaRepository<JourneyStop, Integer> {

	
}
