package ie.tudublin.journeybot.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import ie.tudublin.journeybot.model.Station.StationBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "journey_fare")
@Builder
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class JourneyFare {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "origin_id")
    private Station originStation;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "destination_id")
    private Station destStation;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "fare_id")
	private Fare fare;
	
	

}
