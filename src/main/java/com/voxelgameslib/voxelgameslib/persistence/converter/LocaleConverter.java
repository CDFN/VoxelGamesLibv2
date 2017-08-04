package com.voxelgameslib.voxelgameslib.persistence.converter;

import com.voxelgameslib.voxelgameslib.lang.Locale;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class LocaleConverter implements AttributeConverter<Locale, String> {

    @Override
    public String convertToDatabaseColumn(Locale attribute) {
        return attribute.getTag();
    }

    @Override
    public Locale convertToEntityAttribute(String dbData) {
        return Locale.fromTag(dbData).orElse(Locale.ENGLISH);
    }
}