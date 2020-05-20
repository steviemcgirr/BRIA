package ie.tudublin.journeybot.dto.response;


import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import ie.tudublin.journeybot.model.Transport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JourneyInfoResponse {
	
	private Integer id;
    private String origin;
    private String destination;
	private String operating;
	private Set<String> stops;
	
	public JourneyInfoResponse(Transport journey) {
		this.id = journey.getId();
		this.origin = journey.getStationOriginId().getName();
		this.destination = journey.getStationDestId().getName();
		this.operating = journey.getTransportOperating().getMessage();
	
		
	}
	

}
