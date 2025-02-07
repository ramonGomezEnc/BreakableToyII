package com.flightsearch.backend.mapper;

import com.flightsearch.backend.model.flightoptions.Dictionaries;
import com.flightsearch.backend.model.flightoptions.FlightOffer;
import com.flightsearch.backend.model.flightoptions.TravelerPricing;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FlightOfferDetailMapper {

    private final FlightItineraryMapper itineraryMapper;

    /**
     * Maps flight offers into a detailed response structure.
     *
     * @param itineraryMapper FlightItineraryMapper for mapping itinerary details
     */
    public FlightOfferDetailMapper(FlightItineraryMapper itineraryMapper) {
        this.itineraryMapper = itineraryMapper;
    }

    /**
     * Builds a detailed flight option which includes segments, fares, amenities, and more.
     *
     * @param offer the FlightOffer to map
     * @param dictionaries dictionaries to map carrier/aircraft codes
     * @return a map containing detailed flight information
     */
    public Map<String, Object> buildDetailedFlightOption(
            FlightOffer offer,
            Dictionaries dictionaries
    ) {
        Map<String, Object> detailMap = new LinkedHashMap<>();
        detailMap.put("id", offer.getId());
        detailMap.put("itineraries", itineraryMapper.buildDetailedItineraryData(offer, dictionaries));
        detailMap.put("priceBreakdown", buildPriceBreakdown(offer));
        return detailMap;
    }

    private Map<String, Object> buildPriceBreakdown(FlightOffer offer) {
        Map<String, Object> priceInfo = new LinkedHashMap<>();
        if (offer.getPrice() == null) return priceInfo;
        priceInfo.put("basePrice", offer.getPrice().getBase());
        priceInfo.put("totalPrice", offer.getPrice().getTotal());
        priceInfo.put("currency", offer.getPrice().getCurrency());

        List<Map<String, Object>> feesList = new ArrayList<>();
        if (offer.getPrice().getFees() != null) {
            offer.getPrice().getFees().forEach(fee -> {
                Map<String, Object> feeMap = new HashMap<>();
                feeMap.put("type", fee.getType());
                feeMap.put("amount", fee.getAmount());
                feesList.add(feeMap);
            });
        }
        priceInfo.put("fees", feesList);

        List<Map<String, Object>> pricePerTraveler = new ArrayList<>();
        if (offer.getTravelerPricings() != null) {
            for (TravelerPricing tp : offer.getTravelerPricings()) {
                Map<String, Object> travelerPriceMap = new LinkedHashMap<>();
                travelerPriceMap.put("travelerId", tp.getTravelerId());
                travelerPriceMap.put("travelerType", tp.getTravelerType());
                if (tp.getPrice() != null) {
                    travelerPriceMap.put("price", tp.getPrice().getTotal());
                }
                pricePerTraveler.add(travelerPriceMap);
            }
        }
        priceInfo.put("pricePerTraveler", pricePerTraveler);
        return priceInfo;
    }
}
