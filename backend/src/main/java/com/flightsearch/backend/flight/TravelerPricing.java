package com.flightsearch.backend.flight;

import lombok.Getter;
import java.util.List;

public class TravelerPricing {
    private String travelerId;
    private String fareOption;
    private String travelerType;
    private Price price;
    private List<FareDetailsBySegment> fareDetailsBySegment;

    @Getter
    public static class FareDetailsBySegment {
        private String segmentId;
        private String cabin;
        private String fareBasis;
        private String brandedFare;
        private String brandedFareLabel;
        private String className;
        private IncludedCheckedBags includedCheckedBags;
        private List<Amenity> amenities;

        @Getter
        public static class IncludedCheckedBags {
            private int quantity;
        }

        @Getter
        public static class Amenity {
            private String description;
            private boolean isChargeable;
            private String amenityType;
            private AmenityProvider amenityProvider;
        }

        @Getter
        public static class AmenityProvider {
            private String name;
        }
    }

}
