package com.flightsearch.backend.mapper;

import com.flightsearch.backend.model.flightoptions.Dictionaries;
import com.flightsearch.backend.model.flightoptions.FlightOffer;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FlightOfferMapper {

    private final FlightItineraryMapper itineraryMapper;

    /**
     * Maps flight offers into a summarized (essential) response structure.
     *
     * @param itineraryMapper FlightItineraryMapper for itinerary data
     */
    public FlightOfferMapper(FlightItineraryMapper itineraryMapper) {
        this.itineraryMapper = itineraryMapper;
    }

    /**
     * Builds a list of flights in an essential format. Each flight can have multiple itineraries
     * if it is a round-trip or multi-city flight.
     *
     * @param flightOffers list of FlightOffer objects
     * @param dictionaries dictionary data for carriers/aircraft
     * @return list of mapped flights with ID, itineraries summary, and price info
     */
    public List<Map<String, Object>> buildEssentialFlightList(
            List<FlightOffer> flightOffers,
            Dictionaries dictionaries
    ) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (flightOffers == null || flightOffers.isEmpty()) {
            return result;
        }
        for (FlightOffer offer : flightOffers) {
            if (offer.getItineraries() == null || offer.getItineraries().isEmpty()) {
                continue;
            }
            Map<String, Object> flightMap = new LinkedHashMap<>();
            flightMap.put("id", offer.getId());
            flightMap.put("itineraries",
                    itineraryMapper.buildEssentialItineraryData(offer.getItineraries(), dictionaries));
            if (offer.getPrice() != null) {
                flightMap.put("totalPrice", offer.getPrice().getGrandTotal());
                flightMap.put("currency", offer.getPrice().getCurrency());
            }
            if (offer.getTravelerPricings() != null && !offer.getTravelerPricings().isEmpty()) {
                flightMap.put("pricePerTraveler",
                        offer.getTravelerPricings().getFirst().getPrice().getTotal());
            }
            result.add(flightMap);
        }
        return result;
    }
}
