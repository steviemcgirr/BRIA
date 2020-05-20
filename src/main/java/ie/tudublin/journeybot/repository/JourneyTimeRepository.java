package ie.tudublin.journeybot.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.criteria.From;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import ie.tudublin.journeybot.model.Station;
import ie.tudublin.journeybot.model.Time;
import ie.tudublin.journeybot.model.Transport;


@Repository
public interface JourneyTimeRepository extends JpaRepository<Time, Integer> {

	@Query("select t from Time as t join Transport as ts on ts.id = t.transport where ts.transportOperating = ie.tudublin.journeybot.enumerated.TransportOperating.MONTOSAT "
			+ "and ts.stationDestId = ?1 and t.station = ?2 and t.departTime < ?3 ORDER BY t.departTime ASC")
	List<Time> findallByDestStation(Optional<Station> stationDestId, Station station, String timeString);
	
	@Query("select t from Time as t join Transport as ts on t.transport = ts.id join JourneyStop j on ts.id = j.transport "
			+ "where t.station=?1 and j.stop=?2 and ts.transportOperating = ie.tudublin.journeybot.enumerated.TransportOperating.MONTOSAT "
			+ "and t.departTime > ?3 and ts.stationOriginId=?4 ORDER BY t.departTime ASC")
	Optional<List<Time>> findTrainsRunningToDestStation(Station fromStation, Station toStation, String timeString, Station originStation);

	List<Time> findByTransportOrderByDepartTimeAsc(Optional<Transport> transport);

	Time findByTransportAndStation(Optional<Transport> transport, Station station);

	@Query("select t from Time as t join Transport as ts on t.transport = ts.id join JourneyStop j on ts.id = j.transport "
			+ "where t.station=?1 and j.stop=?2 "
			+ "and t.departTime > ?3 and ts.stationOriginId=?4 ORDER BY t.departTime ASC")
	Optional<List<Time>> findBusesRunningToDestStation(Station fromStation, Station toStation, String timeString,
			Station origiStation);
     
	@Query("select t from Time as t join Transport as ts on t.transport = ts.id join JourneyStop j on ts.id = j.transport "
			+ "where t.station=?1 and j.stop=?2 and ts.transportOperating = ie.tudublin.journeybot.enumerated.TransportOperating.SUNONLY "
			+ "and t.departTime > ?3 and ts.stationOriginId=?4 ORDER BY t.departTime ASC")
	Optional<List<Time>> findTrainsRunningToDestStationSunday(Station fromStation, Station toStation, String timeString,
			Station origiStation);

	@Query("select t from Time as t join Transport as ts on t.transport = ts.id join JourneyStop j on ts.id = j.transport "
			+ "where t.station=?1 and j.stop=?2 and ts.transportOperating = ie.tudublin.journeybot.enumerated.TransportOperating.SUNONLY "
			+ "and ts.stationOriginId=?3 ORDER BY t.departTime ASC")
	Optional<List<Time>> findAllTrainsRunningToDestStationSunday(Station station, Station station2, Station station3);

	
	@Query("select t from Time as t join Transport as ts on t.transport = ts.id join JourneyStop j on ts.id = j.transport "
			+ "where t.station=?1 and j.stop=?2 "
			+ "and ts.stationOriginId=?3 ORDER BY t.departTime ASC")
	Optional<List<Time>> findAllBusesRunningToDestStation(Station station, Station station2, Station station3);
}


