package ie.tudublin.journeybot.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.common.collect.Iterables;

import ie.tudublin.journeybot.dto.response.JourneyStopsResponse;
import ie.tudublin.journeybot.dto.response.JourneyTimesResponse;
import ie.tudublin.journeybot.enumerated.TransportType;
import ie.tudublin.journeybot.model.Fare;
import ie.tudublin.journeybot.model.JourneyFare;
import ie.tudublin.journeybot.model.Station;
import ie.tudublin.journeybot.model.Time;
import ie.tudublin.journeybot.model.Transport;
import ie.tudublin.journeybot.repository.StationRepository;
import ie.tudublin.journeybot.repository.JourneyInfoRepository;
import ie.tudublin.journeybot.repository.JourneyTimeRepository;

@Service
public class JourneyTimeService {

	private static final String SLIGO = "Sligo";
	private static final String DUBLIN_CONNOLLY = "Dublin Connolly";
	private static final String TRAIN = "train";
	private static final String BUS = "bus";
	private static final String DONEGAL = "Donegal (Abbey Hotel)";
	private static final String DUBLIN_BUSARUS = "Dublin Busáras";
	private static final String SUNDAY = "SUNDAY";
	private static final String DEPARTING = "departing";
	private static final String NEXT_TRAIN = "next train";
	private static final String LAST_TRAIN = "last train";
	private static final String NEXT_BUS = "next bus";
	private static final String LAST_BUS = "last bus";

	private final JourneyTimeRepository journeyTimeRepository;
	private final StationRepository stationRepository;
	private final JourneyInfoRepository journeyInfoRepository;
	private final JourneyFareService journeyFareService;
	private final FareService fareService;

	@Autowired
	public JourneyTimeService(JourneyTimeRepository journeyTimeRepository, StationRepository stationRepository,
			JourneyInfoService journeyInfoService, JourneyInfoRepository journeyInfoRepository,
			JourneyFareService journeyFareService, FareService fareService) {
		this.journeyTimeRepository = journeyTimeRepository;
		this.stationRepository = stationRepository;
		this.journeyInfoRepository = journeyInfoRepository;
		this.journeyFareService = journeyFareService;
		this.fareService = fareService;

	}

	private JourneyTimesResponse buildJourneyTimesResponse(Time time, String toStation) {
		return new JourneyTimesResponse(time, toStation);
	}

	private JourneyStopsResponse buildJourneyStopsResponse(Time time, Integer fromStation, Integer toStation) {
		return new JourneyStopsResponse(time, fromStation, toStation);
	}

	public String getStationById() {
		Optional<Station> stationName = stationRepository.findById(14);
		return stationName.get().getName();

	}

	public ObjectNode findJourneyTimes(QueryResult queryResult, String departingFrom) throws Exception {
		Optional<List<Time>> times;
		JourneyTimesResponse journeyTimesResponse = null;
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode objectNode = objectMapper.createObjectNode();
		Boolean trainsRunningBoolean = null;
		String reply = null;
		LocalTime timeNow = LocalTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		LocalDate currentdate = LocalDate.now();
		Optional<Station> fromStation = stationRepository.findByName(departingFrom);
		Optional<Station> toStation = stationRepository
				.findByName(queryResult.getParameters().getFieldsOrThrow("stations").getStringValue());
		String timeString = timeNow.format(formatter);
		Optional<Station> orginStation;
		String transportType;
         
		// make call to DB for trains
		if (queryResult.getParameters().getFieldsOrThrow(DEPARTING).getStringValue().equals(NEXT_TRAIN)
				|| queryResult.getParameters().getFieldsOrThrow(DEPARTING).getStringValue().equals(LAST_TRAIN)) {
			transportType = TRAIN;
			//set origin station 
			if (fromStation.get().getId() < toStation.get().getId()) {
				orginStation = stationRepository.findByName(SLIGO);
			} else {
				orginStation = stationRepository.findByName(DUBLIN_CONNOLLY);
			}
			if (currentdate.getDayOfWeek().name().equals(SUNDAY)) {
				times = journeyTimeRepository.findTrainsRunningToDestStationSunday(fromStation.get(), toStation.get(),
						timeString, orginStation.get());
			} else {
				times = journeyTimeRepository.findTrainsRunningToDestStation(fromStation.get(), toStation.get(),
						timeString, orginStation.get());
			}
		} else {// make call to DB for buses
			transportType = BUS;
			//set origin station 
			if (toStation.get().getName().equals(DUBLIN_CONNOLLY)) {
				toStation = stationRepository.findByName(DUBLIN_BUSARUS);
			}
			if (fromStation.get().getId() < toStation.get().getId()) {
				orginStation = stationRepository.findByName(DONEGAL);
			} else {
				orginStation = stationRepository.findByName(DUBLIN_BUSARUS);
			}
			times = journeyTimeRepository.findBusesRunningToDestStation(fromStation.get(), toStation.get(), timeString,
					orginStation.get());
		}
         
		 
		if (queryResult.getParameters().getFieldsOrThrow(DEPARTING).getStringValue().equals(NEXT_TRAIN)
				|| queryResult.getParameters().getFieldsOrThrow(DEPARTING).getStringValue().equals(NEXT_BUS)) {
			return getNextJourneyTime(times, objectNode, trainsRunningBoolean, journeyTimesResponse, timeString,
					fromStation.get(), toStation.get(), reply, transportType, queryResult, orginStation.get());
			
		} else if (queryResult.getParameters().getFieldsOrThrow(DEPARTING).getStringValue().equals(LAST_TRAIN)
				|| queryResult.getParameters().getFieldsOrThrow(DEPARTING).getStringValue().equals(LAST_BUS)) {
			return getLastJourneyTime(times, objectNode, trainsRunningBoolean, journeyTimesResponse, timeString,
					fromStation.get(), toStation.get(), reply, transportType, queryResult, orginStation.get());
		}

		return objectNode;
	}

	private ObjectNode getLastJourneyTime(Optional<List<Time>> times, ObjectNode objectNode,
			Boolean trainsRunningBoolean, JourneyTimesResponse journeyTimesResponse, String timeString,
			Station fromStation, Station toStation, String reply, String transportType, QueryResult queryResult,
			Station originStation) {
		if (times.isPresent()) {
			for (Time time : times.get()) {
				if (time.getDepartTime() != "|") {
					Time lastJourneyTime = Iterables.getLast(times.get(), null);
					journeyTimesResponse = buildJourneyTimesResponse(lastJourneyTime, toStation.getName());
					break;
				}
			}
			trainsRunningBoolean = true;
			reply = "🤖 The last " + journeyTimesResponse.getTransportType() + " to " + journeyTimesResponse.getToStation()
					+ " is at " + journeyTimesResponse.getTime() + " departing from "
					+ journeyTimesResponse.getStationName() + ".";
			objectNode.put("message", reply);
			objectNode.put("transportRunning", trainsRunningBoolean);
			System.out.println("Transposrt id" + journeyTimesResponse.getTransportId());
			objectNode.put("journeyId", journeyTimesResponse.getTransportId());
			objectNode.put("fromStation", fromStation.getId());
			objectNode.put("toStation", toStation.getId());
			objectNode.put("transportType", transportType);
			objectNode.put("originStation", originStation.getId());
		} else {
			trainsRunningBoolean = false;
			reply = "🤖 Sorry but there is no more " + transportType + " running to " + toStation.getName() + " today.";
			objectNode.put("message", reply);
			objectNode.put("transportRunning", trainsRunningBoolean);
			objectNode.put("fromStation", fromStation.getId());
			objectNode.put("toStation", toStation.getId());
			objectNode.put("transportType", transportType);
			objectNode.put("originStation", originStation.getId());
		}
		return objectNode;
	}

	public String laterJourneyTimes(ObjectNode objectNode) {
		Optional<List<Time>> times = null;
		LocalDate currentdate = LocalDate.now();
		LocalTime timeNow = LocalTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		String timeString = timeNow.format(formatter);
		JourneyTimesResponse journeyTimesResponse;
		Optional<Station> fromStation = stationRepository.findById(objectNode.get("fromStation").intValue());
		Optional<Station> toStation = stationRepository.findById(objectNode.get("toStation").intValue());
		Optional<Station> originStation = stationRepository.findById(objectNode.get("originStation").intValue());
		String transportType = objectNode.get("transportType").textValue();
		
		//build reply
		StringBuilder message = new StringBuilder(
				"🤖 All depatures for the remainder of the day to " + toStation.get().getName() + ": \n\n");
		if (transportType == TRAIN) {//if transport type is train 
			if (currentdate.getDayOfWeek().name().equals(SUNDAY)) {//if day of the week is Sunday 
				times = journeyTimeRepository.findTrainsRunningToDestStationSunday(fromStation.get(), toStation.get(),
						timeString, originStation.get());
			} else {
				times = journeyTimeRepository.findTrainsRunningToDestStationSunday(fromStation.get(), toStation.get(),
						timeString, originStation.get());
			}
		} else {//if transport type is bus
			times = journeyTimeRepository.findBusesRunningToDestStation(fromStation.get(), toStation.get(), timeString,
					originStation.get());
		}
		if (times.isPresent()) {
			for (Time time : times.get()) {
				journeyTimesResponse = buildJourneyTimesResponse(time, toStation.get().getName());
				message.append("- " + journeyTimesResponse.getTime() + "\n");
			}

		} else {
			message.append("🤖 Sorry but there is no more " + transportType + " running to " + toStation.get().getName()
					+ " today.");
		}
		return message.toString();
	}

	public String getJourneyTimesTomorrow(ObjectNode objectNode) {
		Optional<List<Time>> times = null;
		LocalDate currentdate = LocalDate.now();
		JourneyTimesResponse journeyTimesResponse;
		System.out.println("origin station " + objectNode.get("originStation").intValue());

		Optional<Station> fromStation = stationRepository.findById(objectNode.get("fromStation").intValue());
		Optional<Station> toStation = stationRepository.findById(objectNode.get("toStation").intValue());
		Optional<Station> originStation = stationRepository.findById(objectNode.get("originStation").intValue());
		String transportType = objectNode.get("transportType").textValue();
		System.out.println("from station " + fromStation.get().getId() + " to station " + toStation.get().getId()
				+ " origin station: " + originStation.get().getId());
		StringBuilder message = new StringBuilder(
				"🤖 All depatures times tomorrow destined for " + toStation.get().getName() + ": \n\n");
		if (transportType == TRAIN) {
			if (currentdate.getDayOfWeek().name().equals(SUNDAY)) {
				times = journeyTimeRepository.findAllTrainsRunningToDestStationSunday(fromStation.get(),
						toStation.get(), originStation.get());
			} else {
				times = journeyTimeRepository.findAllTrainsRunningToDestStationSunday(fromStation.get(),
						toStation.get(), originStation.get());
			}
		} else {
			times = journeyTimeRepository.findAllBusesRunningToDestStation(fromStation.get(), toStation.get(),
					originStation.get());
		}
		for (Time time : times.get()) {
			journeyTimesResponse = buildJourneyTimesResponse(time, toStation.get().getName());
			message.append("- " + journeyTimesResponse.getTime() + "\n");
		}
		return message.toString();

	}

	private ObjectNode getNextJourneyTime(Optional<List<Time>> times, ObjectNode objectNode,
			Boolean trainsRunningBoolean, JourneyTimesResponse journeyTimesResponse, String timeString,
			Station fromStation, Station toStation, String reply, String transportType, QueryResult queryResult,
			Station originStation) {
        
		//if there is departures remaining for the day
		if (times.isPresent()) {
			for (Time time : times.get()) {
				//add the departing time greater than current time to the response and break out of loop
				if (time.getDepartTime().compareTo(timeString) > 0 && time.getDepartTime() != "|") {
					journeyTimesResponse = buildJourneyTimesResponse(time, toStation.getName());
					System.out.println("times : " + time.getDepartTime());
					break;
				}
			}
			trainsRunningBoolean = true;
			reply = "🤖 The next " + journeyTimesResponse.getTransportType() + " to " + journeyTimesResponse.getToStation()
					+ " is at " + journeyTimesResponse.getTime() + " departing from "
					+ journeyTimesResponse.getStationName() + ".";
			objectNode.put("message", reply);
			objectNode.put("transportRunning", trainsRunningBoolean);
			System.out.println("Transposrt id" + journeyTimesResponse.getTransportId());
			objectNode.put("journeyId", journeyTimesResponse.getTransportId());
			objectNode.put("fromStation", fromStation.getId());
			objectNode.put("toStation", toStation.getId());
			objectNode.put("transportType", transportType);
			objectNode.put("originStation", originStation.getId());
		} else {//if there is no departures remaining for the day
			trainsRunningBoolean = false;
			reply = "🤖 Sorry but there is no more " + transportType + " running to " + toStation.getName() + " today.";
			objectNode.put("message", reply);
			objectNode.put("transportRunning", trainsRunningBoolean);
			objectNode.put("fromStation", fromStation.getId());
			objectNode.put("toStation", toStation.getId());
			objectNode.put("transportType", transportType);
			System.out.println("test the bag of this :" + originStation.getId());
			objectNode.put("originStation", originStation.getId());
		}
		return objectNode;
	}

	public String getAllTimesAndStops(Integer id, Integer fromStation, Integer toStation) {
		Optional<Transport> transport = journeyInfoRepository.findById(id);
		Optional<Station> stationDepart = stationRepository.findById(fromStation);
		Time fromStationTimeId = journeyTimeRepository.findByTransportAndStation(transport, stationDepart.get());
		Optional<Station> stationArrive = stationRepository.findById(toStation);
		Time toStationTimeIdTime = journeyTimeRepository.findByTransportAndStation(transport, stationArrive.get());
		List<Time> times = journeyTimeRepository.findByTransportOrderByDepartTimeAsc(transport);
		// get stops
		List<JourneyStopsResponse> journeyStopsResponses = times.stream()
				.map(time -> buildJourneyStopsResponse(time, fromStationTimeId.getId(), toStationTimeIdTime.getId()))
				.collect(Collectors.toList());
		// get time arriving
		String timeArriving = journeyStopsResponses.stream()
				.filter(jsr -> toStationTimeIdTime.getStation().getName().equals(jsr.getStop())).findAny().orElse(null)
				.getTime();
		// get service
		String service = journeyStopsResponses.stream()
				.filter(jsr -> toStationTimeIdTime.getStation().getName().equals(jsr.getStop())).findAny().orElse(null)
				.getOperating();
		// Append time of arrival to reply message
		String arrivingAndServiceNumber = "🤖 Arrving in " + stationArrive.get().getName() + " at " + timeArriving
				+ "\n\nService: " + service;

		// get fare for journey and append to reply message
		JourneyFare journeyFare = journeyFareService.getJourneyFare(stationDepart.get(), stationArrive.get());
		Optional<Fare> fare = fareService.findById(journeyFare.getFare().getId());
		Fare farePrices = fare.get();
		String prices = "\n\n*Ticket Price Breakdown*\nAdult Single: " + farePrices.getAdultSingle()
				+ "\nAdult Day return: " + farePrices.getAdultDayReturn() + "\nAdult Open Return: "
				+ farePrices.getAdultOpenReturn() + "\nStudent Single: " + farePrices.getStudentSingle()
				+ "\nStudent Return: " + farePrices.getStudenReturn() + "\n";

		// append stops a long route to reply
		StringBuilder message = new StringBuilder(arrivingAndServiceNumber + "\n\n*Stops along route:* \n\n");
		for (JourneyStopsResponse journeyStopsResponse : journeyStopsResponses) {
			System.out.println(journeyStopsResponse.getTransportType() + ":" + TransportType.TRAIN.getMessage());
			if (journeyStopsResponse.getTransportType().equals(TransportType.TRAIN.getMessage())) {
				System.out.println("must be train");
				if (journeyStopsResponse.getId() >= fromStationTimeId.getId()
						&& journeyStopsResponse.getId() <= toStationTimeIdTime.getId()
						|| journeyStopsResponse.getId() <= fromStationTimeId.getId()
								&& journeyStopsResponse.getId() >= toStationTimeIdTime.getId()) {
					message.append(journeyStopsResponse.getStop() + " : " + journeyStopsResponse.getTime() + "\n");
					System.out.println(journeyStopsResponse.getStop() + " : " + journeyStopsResponse.getTime());
				}
			} else {
				if (journeyStopsResponse.getId() >= fromStationTimeId.getId()
						&& journeyStopsResponse.getId() <= toStationTimeIdTime.getId()
						|| journeyStopsResponse.getId() <= fromStationTimeId.getId()
								&& journeyStopsResponse.getId() >= toStationTimeIdTime.getId()) {
					if (journeyStopsResponse.getTime().equals("NS")) {
						break;
					}
					message.append(journeyStopsResponse.getStop() + " : " + journeyStopsResponse.getTime() + "\n");
					System.out.println(journeyStopsResponse.getStop() + " : " + journeyStopsResponse.getTime());
				}
			}
		}
		message.append(prices);
		message.append("\n\nWould you like to book a seat?");
		return message.toString();
	}

}
