package ie.tudublin.journeybot.dto.response;

import java.sql.Time;

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
public class JourneyTimesResponse {

	
	private Integer id;
    private String stationName;
	private String fromStation;
	private String toStation;
	private String time;
	private String operating;
	private Integer transportId;
	private String transportType;
	
	
	
	
	public JourneyTimesResponse(ie.tudublin.journeybot.model.Time time, String destStation) {
		this.id = time.getId();
		this.stationName = time.getStation().getName();
		this.fromStation = time.getTransport().getStationOriginId().getName();
		this.toStation = destStation;
		this.time = time.getDepartTime();
		this.operating = time.getTransport().getTransportOperating().getMessage();
		this.transportId = time.getTransport().getId();
		this.transportType = time.getTransport().getTransportType().getMessage();
		
	}
	
	
	
}
