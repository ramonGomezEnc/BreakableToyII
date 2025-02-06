package com.flightsearch.backend.model.flightoptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TravelerPricing {
    private String travelerId;
    private String fareOption;
    private String travelerType;
    private Price price;
    private List<FareDetailsBySegment> fareDetailsBySegment;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FareDetailsBySegment {
        private String segmentId;
        private String cabin;
        private String fareBasis;
        private String brandedFare;
        private String brandedFareLabel;
        private String className;
        private IncludedCheckedBags includedCheckedBags;
        private List<Amenity> amenities;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class IncludedCheckedBags {
            private int quantity;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Amenity {
            private String description;
            private boolean isChargeable;
            private String amenityType;
            private AmenityProvider amenityProvider;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class AmenityProvider {
            private String name;
        }
    }

}
