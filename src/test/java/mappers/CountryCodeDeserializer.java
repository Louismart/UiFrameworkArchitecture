package com.worldremit.test.web.services.mappers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.worldremit.test.web.models.codes.CountryCode;
import com.worldremit.test.web.models.codes.CountryCodesGetter;
import java.io.IOException;
import java.util.Objects;
import lombok.val;

public class CountryCodeDeserializer extends StdDeserializer<CountryCode> {

    protected CountryCodeDeserializer(final Class<?> vc) {
        super(vc);
    }

    public CountryCodeDeserializer() {
        this(null);
    }

    @Override
    public CountryCode deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        val countryValue = jp.getValueAsString();
        if (Objects.isNull(countryValue) || countryValue.equals("")) {
            return null;
        } else {
            return CountryCodesGetter.getCountryCode(countryValue);
        }
    }
}
