package ie.tudublin.journeybot.enumerated;

import java.util.HashMap;
import java.util.Map;



public enum TransportOperating {
	
	MONTOFRI(0, "Mon to Fri"), MONTOSAT(1, "Mon To Sat"), SATONLY(2, "Sat Only"), 
	SUNONLY(3, "Sun Only "), XTHIRTY(4, "X30"), THIRTY(5, "30");

	TransportOperating(Integer index, String message) {
		this.index = index;
		this.message = message;
	}

	private Integer index;
	private String message;

	private static final Map<Integer, TransportOperating> intTotypeMap = new HashMap<>();

	static {
		for (TransportOperating transportOperating : TransportOperating.values()) {
			intTotypeMap.put(transportOperating.index, transportOperating);
		}
	}

	public static TransportOperating fromIndex(Integer index) {
		return intTotypeMap.get(index);
	}

	public Integer getIndex() {
		return index;
	}

	public String getMessage() {
		return message;
	}

}
