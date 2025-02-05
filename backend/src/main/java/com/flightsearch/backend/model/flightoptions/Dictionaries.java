package com.flightsearch.backend.model.flightoptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Dictionaries {
    private Map<String, LocationDictionary> locations;
    private Map<String, String> aircraft;
    private Map<String, String> currencies;
    private Map<String, String> carriers;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LocationDictionary {
        private String cityCode;
        private String countryCode;
    }
}
