package ca.powercool.powercoolhub.converters;

import org.springframework.core.convert.converter.Converter;
import java.sql.Date;

public class StringToDateConverter implements Converter<String, Date> {
    @Override
    public Date convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        return Date.valueOf(source);
    }
}