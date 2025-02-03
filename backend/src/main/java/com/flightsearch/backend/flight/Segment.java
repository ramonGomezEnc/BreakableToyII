package com.flightsearch.backend.flight;

import lombok.Getter;

@Getter
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

    @Getter
    public static class Departure {
        private String iataCode;
        private String terminal;
        private String at; // datetime
    }

    @Getter
    public static class Arrival {
        private String iataCode;
        private String terminal;
        private String at;
    }

    @Getter
    public static class Aircraft {
        private String code;
    }

    @Getter
    public static class Operating {
        private String carrierCode;
    }
}
