package com.flightsearch.backend.flight.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Segment {
    private Departure departure;
    private Arrival arrival;
    private String carrierCode;
    private String number;
    private Aircraft aircraft;
    private Operating operating;
    private String duration;
    private String id;
    private int numberOfStops;
    private boolean blacklistedInEU;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Departure {
        private String iataCode;
        private String terminal;
        private String at; // datetime
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Arrival {
        private String iataCode;
        private String terminal;
        private String at;
    }

    @Data
    public static class Aircraft {
        private String code;
    }

    @Data
    public static class Operating {
        private String carrierCode;
    }
}
