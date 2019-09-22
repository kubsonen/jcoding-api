package pl.jcoding.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pl.jcoding.entity.ItemGallery;

@Component
public class ConverterStringToItemGallery implements Converter<String, ItemGallery> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public ItemGallery convert(String s) {
        return objectMapper.readValue(s, ItemGallery.class);
    }
}
