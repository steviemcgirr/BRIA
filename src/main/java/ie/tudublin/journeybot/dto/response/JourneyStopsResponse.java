package ie.tudublin.journeybot.dto.response;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import ie.tudublin.journeybot.model.Time;
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
public class JourneyStopsResponse {
	
	
	private Integer id;
    private String stop;
	private String origin;
	private String destination;
	private String time;
	private String operating;
	private Integer transportId;
	private String transportType;
	
	
	public JourneyStopsResponse(Time time, Integer fromStation, Integer toStation) {
		 
		
		  System.out.println("STATIONS : "+time.getId()+ " "+time.getStation().getName() +" FROM STATION "+fromStation +" to station"+toStation);
		
			this.id = time.getId();
			this.stop = time.getStation().getName();
			this.time = time.getDepartTime();
			this.operating =  time.getTransport().getTransportOperating().getMessage();
			this.transportType = time.getTransport().getTransportType().getMessage();
			this.origin = time.getTransport().getStationOriginId().getName();
			this.destination =  time.getTransport().getStationDestId().getName();
		
			
	
	}

}
