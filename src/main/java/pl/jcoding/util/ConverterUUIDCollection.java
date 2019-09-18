package pl.jcoding.util;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.*;
import java.util.stream.Collectors;

@Converter
public class ConverterUUIDCollection implements AttributeConverter<List<UUID>, String> {

    private static final String SEPARATOR = ",";


    @Override
    public String convertToDatabaseColumn(List<UUID> uuids) {
        return Optional.ofNullable(uuids)
                .filter(us -> !us.isEmpty())
                .map(us -> StringUtils.join(us, SEPARATOR))
                .orElse(null);
    }

    @Override
    public List<UUID> convertToEntityAttribute(String s) {
        return Optional.ofNullable(s)
                .map(s1 -> Arrays.stream(s1.split(SEPARATOR)).map(s2 -> UUID.fromString(s2)).collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }

}
