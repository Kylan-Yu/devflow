package com.devflow.api.modules.user.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LanguagePreferenceConverter implements AttributeConverter<LanguagePreference, String> {

    @Override
    public String convertToDatabaseColumn(LanguagePreference attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public LanguagePreference convertToEntityAttribute(String dbData) {
        return dbData == null ? null : LanguagePreference.fromValue(dbData);
    }
}
