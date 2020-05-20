package ie.tudublin.journeybot.model;

import java.util.Set;

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
@Table(name = "journey_stops", schema = "public")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JourneyStop {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "transport_id")
    private Transport transport;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stop_id")
    private Station stop;

}
