package ie.tudublin.journeybot.enumerated.converter;

import javax.persistence.AttributeConverter;

import ie.tudublin.journeybot.enumerated.TransportType;

public class TransportTypeConverter implements AttributeConverter<TransportType, Integer> {

	@Override
	public Integer convertToDatabaseColumn(TransportType attribute) {
		return attribute.getIndex();
	}

	@Override
	public TransportType convertToEntityAttribute(Integer dbData) {
		return TransportType.fromIndex(dbData);
	}

}
