package com.flightsearch.backend.flight.model;

import lombok.Data;
import java.util.Map;

@Data
public class Dictionaries {
    private Map<String, LocationDictionary> locations;
    private Map<String, String> aircraft;
    private Map<String, String> currencies;
    private Map<String, String> carriers;

    @Data
    public static class LocationDictionary {
        private String cityCode;
        private String countryCode;
    }
}
