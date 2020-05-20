package ie.tudublin.journeybot.model;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;



import ie.tudublin.journeybot.enumerated.TransportOperating;
import ie.tudublin.journeybot.enumerated.TransportType;
import ie.tudublin.journeybot.enumerated.converter.TransportOperatingConverter;
import ie.tudublin.journeybot.enumerated.converter.TransportTypeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "transport", schema = "public")
public class Transport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "origin_id")
    private Station stationOriginId;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "destination_id")
    private Station stationDestId;
	
	@Column(name = "operating")
	@Convert(converter = TransportOperatingConverter.class)
	private TransportOperating transportOperating;
	
	@Column(name = "transport_type")
	@Convert(converter = TransportTypeConverter.class)
	private TransportType transportType;
	

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "transport")
    private Set<JourneyStop> journeyStops;
	
	
}