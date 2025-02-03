package com.flightsearch.backend.flight.model;

import lombok.Data;
import java.util.List;

@Data
public class FlightOffer {
    private String type;
    private String id;
    private String source;
    private boolean instantTicketingRequired;
    private boolean nonHomogeneous;
    private boolean oneWay;
    private boolean isUpsellOffer;
    private String lastTicketingDate;
    private String lastTicketingDateTime;
    private int numberOfBookableSeats;
    private List<Itinerary> itineraries;
    private Price price;
    private PricingOptions pricingOptions;
    private List<String> validatingAirlineCodes;
    private List<TravelerPricing> travelerPricings;

    @Data
    public static class PricingOptions {
        private List<String> fareType;
        private boolean includedCheckedBagsOnly;
    }
}
