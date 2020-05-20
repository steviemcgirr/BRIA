package ie.tudublin.journeybot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ie.tudublin.journeybot.model.Fare;

public interface FareRepository extends JpaRepository<Fare, Integer>{

}
