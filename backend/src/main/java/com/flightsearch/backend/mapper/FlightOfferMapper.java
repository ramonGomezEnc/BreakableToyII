package com.flightsearch.backend.mapper;

import com.flightsearch.backend.model.Airport;
import com.flightsearch.backend.model.flightoptions.Dictionaries;
import com.flightsearch.backend.model.flightoptions.FlightOffer;
import com.flightsearch.backend.model.flightoptions.Segment;
import com.flightsearch.backend.utils.DurationUtils;
import com.flightsearch.backend.client.AmadeusClient;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

@Component
public class FlightOfferMapper {

    private final DurationUtils durationUtils;
    private final AmadeusClient amadeusClient;
    private final Map<String, String> airportCache = new HashMap<>();

    public FlightOfferMapper(DurationUtils durationUtils, AmadeusClient amadeusClient) {
        this.durationUtils = durationUtils;
        this.amadeusClient = amadeusClient;
    }

    private String getAirportName(String airportCode) {
        String airportName = airportCache.get(airportCode);
        if (airportName == null) {
            try {
                Airport airport = amadeusClient.fetchAirport(airportCode);
                airportName = airport.getName();
                airportCache.put(airportCode, airportName);
            } catch (Exception e) {
                airportName = null;
            }
        }
        return airportName;
    }

    public List<Map<String, Object>> buildEssentialFlightList(List<FlightOffer> flightOffers, Dictionaries dictionaries) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (flightOffers == null || flightOffers.isEmpty()) {
            return result;
        }

        for (FlightOffer offer : flightOffers) {
            if (offer.getItineraries() == null || offer.getItineraries().isEmpty()) {
                continue;
            }

            // ID
            Map<String, Object> flightMap = new LinkedHashMap<>();
            flightMap.put("id", offer.getId());

            var itinerary = offer.getItineraries().getFirst();
            var firstSegment = itinerary.getSegments().getFirst();
            var lastSegment = itinerary.getSegments().getLast();

            // Initial and Final Times
            flightMap.put("initialDeparture", firstSegment.getDeparture().getAt());
            flightMap.put("finalArrival", lastSegment.getArrival().getAt());

            // Airports
            var departureAirportCode = firstSegment.getDeparture().getIataCode();
            flightMap.put("departureAirportCode", departureAirportCode);
            flightMap.put("departureAirportName", getAirportName(departureAirportCode));

            var arrivalAirportCode = lastSegment.getArrival().getIataCode();
            flightMap.put("arrivalAirportCode", arrivalAirportCode);
            flightMap.put("arrivalAirportName", getAirportName(arrivalAirportCode));

            // Main Airline
            var mainAirlineCode = firstSegment.getCarrierCode();
            String mainAirlineName = (dictionaries != null && dictionaries.getCarriers() != null)
                    ? dictionaries.getCarriers().getOrDefault(mainAirlineCode, mainAirlineCode)
                    : mainAirlineCode;
            flightMap.put("airlineCode", mainAirlineCode);
            flightMap.put("airlineName", mainAirlineName);

            // Operating Airline
            if (firstSegment.getOperating() != null) {
                var operatingAirlineCode = firstSegment.getOperating().getCarrierCode();
                if (operatingAirlineCode != null && !operatingAirlineCode.equals(mainAirlineCode)) {
                    flightMap.put("operatingAirlineCode", operatingAirlineCode);
                    if (dictionaries != null && dictionaries.getCarriers() != null) {
                        flightMap.put(
                                "operatingAirlineName",
                                dictionaries.getCarriers().getOrDefault(operatingAirlineCode, operatingAirlineCode)
                        );
                    }
                }
            }

            // Total Flight
            flightMap.put("totalFlightTime", durationUtils.formatIsoStringToReadable(itinerary.getDuration()));

            // Stops
            List<Map<String, Object>> stops = new ArrayList<>();
            List<Segment> segments = itinerary.getSegments();
            for (int i = 0; i < segments.size() - 1; i++) {
                var segA = segments.get(i);
                var segB = segments.get(i + 1);
                Duration layover = durationUtils.calculateDurationBetween(
                        segA.getArrival().getAt(),
                        segB.getDeparture().getAt()
                );
                Map<String, Object> stopInfo = new LinkedHashMap<>();
                stopInfo.put("airportCode", segA.getArrival().getIataCode());
                stopInfo.put("layoverTime", durationUtils.formatDurationToReadable(layover));
                stops.add(stopInfo);
            }
            if (!stops.isEmpty()) {
                flightMap.put("stops", stops);
            }

            // Price
            if (offer.getPrice() != null) {
                flightMap.put("totalPrice", offer.getPrice().getGrandTotal());
                flightMap.put("currency", offer.getPrice().getCurrency());
            }

            // Price per Traveler
            if (offer.getTravelerPricings() != null && !offer.getTravelerPricings().isEmpty()) {
                flightMap.put(
                        "pricePerTraveler",
                        offer.getTravelerPricings().getFirst().getPrice().getTotal()
                );
            }

            result.add(flightMap);
        }
        return result;
    }
}