package ie.tudublin.journeybot.enumerated;

import java.util.HashMap;
import java.util.Map;

public enum TransportType {
	

	TRAIN(0, "train"), BUS(1, "bus");

	TransportType(Integer index, String message) {
		this.index = index;
		this.message = message;
	}

	private Integer index;
	private String message;

	private static final Map<Integer, TransportType> intTotypeMap = new HashMap<>();

	static {
		for (TransportType transportType : TransportType.values()) {
			intTotypeMap.put(transportType.index, transportType);
		}
	}

	public static TransportType fromIndex(Integer index) {
		return intTotypeMap.get(index);
	}

	public Integer getIndex() {
		return index;
	}

	public String getMessage() {
		return message;
	}

}
