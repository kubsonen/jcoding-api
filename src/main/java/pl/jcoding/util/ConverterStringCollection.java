package pl.jcoding.util;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.*;
import java.util.stream.Collectors;

@Converter
public class ConverterStringCollection implements AttributeConverter<List<String>, String> {

    private static final String SEPARATOR = ",";


    @Override
    public String convertToDatabaseColumn(List<String> strings) {
        return Optional.ofNullable(strings).filter(s -> !s.isEmpty()).map(s -> StringUtils.join(s, SEPARATOR)).orElse(null);
    }

    @Override
    public List<String> convertToEntityAttribute(String s) {
        return Optional.ofNullable(s).map(ss -> Arrays.stream(ss.split(SEPARATOR)).collect(Collectors.toList())).orElse(new ArrayList<>());
    }
}
