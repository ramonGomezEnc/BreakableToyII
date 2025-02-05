package com.flightsearch.backend.model.flightoptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeneralResponse {
    private Meta meta;
    private List<FlightOffer> data;
    private Dictionaries dictionaries;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Meta {
        private int count;
        private Links links;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Links {
            private String self;
        }
    }
}

