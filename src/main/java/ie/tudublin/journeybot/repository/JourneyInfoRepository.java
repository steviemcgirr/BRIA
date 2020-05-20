package ie.tudublin.journeybot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ie.tudublin.journeybot.model.Transport;


@Repository
public interface JourneyInfoRepository extends JpaRepository<Transport, Integer> {
	

}
