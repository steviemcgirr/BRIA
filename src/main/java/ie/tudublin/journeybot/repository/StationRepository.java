package ie.tudublin.journeybot.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.google.protobuf.Value;

import ie.tudublin.journeybot.model.Station;

public interface StationRepository  extends JpaRepository<Station, Integer>{

	Optional<Station> findByName(String string);
     
}
