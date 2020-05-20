package ie.tudublin.journeybot.enumerated.converter;

import javax.persistence.AttributeConverter;
import ie.tudublin.journeybot.enumerated.TransportOperating;

public class TransportOperatingConverter implements AttributeConverter<TransportOperating, Integer> {

	@Override
	public Integer convertToDatabaseColumn(TransportOperating attribute) {
		// TODO Auto-generated method stub
		return attribute.getIndex();
	}

	@Override
	public TransportOperating convertToEntityAttribute(Integer dbData) {
		// TODO Auto-generated method stub
		return TransportOperating.fromIndex(dbData);
	}

}
