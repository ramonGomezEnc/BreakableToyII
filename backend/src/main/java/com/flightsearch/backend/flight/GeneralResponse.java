package com.flightsearch.backend.flight;

import lombok.Getter;
import java.util.List;
import java.util.Map;

@Getter
public class GeneralResponse {
    private Meta meta;
    private List<FlightOffer> data;
    private Dictionaries dictionaries;

    @Getter
    public static class Meta {
        private int count;
        private Links links;

        @Getter
        public static class Links {
            private String self;
        }
    }

    @Getter
    public static class Dictionaries {
        private Map<String, LocationDictionary> locations;
        private Map<String, String> aircraft;
        private Map<String, String> currencies;
        private Map<String, String> carriers;

        @Getter
        public static class LocationDictionary {
            private String cityCode;
            private String countryCode;
        }
    }

}
