package com.flightsearch.backend.flight.model;

import lombok.Data;
import java.util.List;

@Data
public class GeneralResponse {
    private Meta meta;
    private List<FlightOffer> data;
    private Dictionaries dictionaries;

    @Data
    public static class Meta {
        private int count;
        private Links links;

        @Data
        public static class Links {
            private String self;
        }
    }
}

