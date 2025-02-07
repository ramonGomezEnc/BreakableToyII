package com.flightsearch.backend.mapper;

import com.flightsearch.backend.client.AmadeusClient;
import com.flightsearch.backend.model.Airport;
import com.flightsearch.backend.model.flightoptions.Dictionaries;
import com.flightsearch.backend.model.flightoptions.FlightOffer;
import com.flightsearch.backend.model.flightoptions.Itinerary;
import com.flightsearch.backend.model.flightoptions.Segment;
import com.flightsearch.backend.model.flightoptions.TravelerPricing;
import com.flightsearch.backend.model.flightoptions.TravelerPricing.FareDetailsBySegment;
import com.flightsearch.backend.utils.DurationUtils;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

@Component
public class FlightItineraryMapper {

    private final AmadeusClient amadeusClient;
    private final DurationUtils durationUtils;
    private final Map<String, String> airportCache = new HashMap<>();

    /**
     * Utility class for mapping itineraries into various data structures for
     * essential or detailed information.
     *
     * @param amadeusClient used for fetching airport details if needed
     * @param durationUtils used for formatting and calculating durations
     */
    public FlightItineraryMapper(AmadeusClient amadeusClient, DurationUtils durationUtils) {
        this.amadeusClient = amadeusClient;
        this.durationUtils = durationUtils;
    }

    private String getAirportName(String airportCode) {
        if (airportCache.containsKey(airportCode)) {
            return airportCache.get(airportCode);
        }
        try {
            Airport airport = amadeusClient.fetchAirport(airportCode);
            airportCache.put(airportCode, airport.getName());
            return airport.getName();
        } catch (Exception e) {
            return airportCode;
        }
    }

    private String resolveAirlineName(String airlineCode, Dictionaries dictionaries) {
        if (dictionaries != null && dictionaries.getCarriers() != null) {
            return dictionaries.getCarriers().getOrDefault(airlineCode, airlineCode);
        }
        return airlineCode;
    }

    private String resolveAircraftName(String aircraftCode, Dictionaries dictionaries) {
        if (dictionaries != null && dictionaries.getAircraft() != null) {
            return dictionaries.getAircraft().getOrDefault(aircraftCode, aircraftCode);
        }
        return aircraftCode;
    }

    private Map<String, List<FareDetailsBySegment>> buildFareDetailsMap(FlightOffer offer) {
        Map<String, List<FareDetailsBySegment>> fareDetailsMap = new HashMap<>();
        if (offer.getTravelerPricings() == null) return fareDetailsMap;
        for (TravelerPricing travelerPricing : offer.getTravelerPricings()) {
            if (travelerPricing.getFareDetailsBySegment() == null) continue;
            for (FareDetailsBySegment fareSeg : travelerPricing.getFareDetailsBySegment()) {
                String segIdStr = String.valueOf(fareSeg.getSegmentId());
                fareDetailsMap.putIfAbsent(segIdStr, new ArrayList<>());
                fareDetailsMap.get(segIdStr).add(fareSeg);
            }
        }
        return fareDetailsMap;
    }

    private List<Map<String, Object>> buildAmenitiesList(List<FareDetailsBySegment.Amenity> amenities) {
        List<Map<String, Object>> amenitiesList = new ArrayList<>();
        if (amenities == null) return amenitiesList;
        for (FareDetailsBySegment.Amenity am : amenities) {
            Map<String, Object> amenMap = new LinkedHashMap<>();
            amenMap.put("name", am.getDescription());
            amenMap.put("chargeable", am.isChargeable());
            amenitiesList.add(amenMap);
        }
        return amenitiesList;
    }

    /**
     * Builds a list of itinerary maps for essential (summary) data (no traveler fares).
     * This method includes stops, times, and airline info.
     *
     * @param itineraries the flight itineraries
     * @param dictionaries flight data dictionary for mapping carrier/aircraft codes
     * @return list of itineraries in a summarized structure
     */
    public List<Map<String, Object>> buildEssentialItineraryData(List<Itinerary> itineraries, Dictionaries dictionaries) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Itinerary itinerary : itineraries) {
            Map<String, Object> itineraryMap = new LinkedHashMap<>();
            var segments = itinerary.getSegments();

            if (segments == null || segments.isEmpty()) {
                result.add(itineraryMap);
                continue;
            }
            var firstSegment = segments.getFirst();
            var lastSegment = segments.getLast();

            itineraryMap.put("initialDeparture", firstSegment.getDeparture().getAt());
            itineraryMap.put("finalArrival", lastSegment.getArrival().getAt());

            itineraryMap.put("departureAirportCode", firstSegment.getDeparture().getIataCode());
            itineraryMap.put("departureAirportName", getAirportName(firstSegment.getDeparture().getIataCode()));
            itineraryMap.put("arrivalAirportCode", lastSegment.getArrival().getIataCode());
            itineraryMap.put("arrivalAirportName", getAirportName(lastSegment.getArrival().getIataCode()));

            var mainAirlineCode = firstSegment.getCarrierCode();
            var mainAirlineName = resolveAirlineName(mainAirlineCode, dictionaries);
            itineraryMap.put("airlineCode", mainAirlineCode);
            itineraryMap.put("airlineName", mainAirlineName);

            if (firstSegment.getOperating() != null && firstSegment.getOperating().getCarrierCode() != null) {
                var operatingAirlineCode = firstSegment.getOperating().getCarrierCode();
                if (!operatingAirlineCode.equals(mainAirlineCode)) {
                    itineraryMap.put("operatingAirlineCode", operatingAirlineCode);
                    itineraryMap.put("operatingAirlineName", resolveAirlineName(operatingAirlineCode, dictionaries));
                }
            }
            itineraryMap.put("totalFlightTime", durationUtils.formatIsoStringToReadable(itinerary.getDuration()));

            List<Map<String, Object>> stops = new ArrayList<>();
            for (int i = 0; i < segments.size() - 1; i++) {
                Segment segA = segments.get(i);
                Segment segB = segments.get(i + 1);
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
                itineraryMap.put("stops", stops);
            }
            result.add(itineraryMap);
        }
        return result;
    }

    /**
     * Builds a list of itinerary maps for detailed flight data (includes traveler fares, amenities, etc.).
     *
     * @param offer the full FlightOffer containing traveler pricing
     * @param dictionaries flight data dictionary
     * @return list of itineraries in a detailed structure
     */
    public List<Map<String, Object>> buildDetailedItineraryData(FlightOffer offer, Dictionaries dictionaries) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Itinerary> itineraries = offer.getItineraries();
        if (itineraries == null) return result;
        var fareDetailsMap = buildFareDetailsMap(offer);

        for (Itinerary itinerary : itineraries) {
            Map<String, Object> itineraryMap = new LinkedHashMap<>();
            var segments = itinerary.getSegments();

            if (segments.isEmpty()) {
                result.add(itineraryMap);
                continue;
            }
            var firstSegment = segments.getFirst();
            var lastSegment = segments.getLast();

            itineraryMap.put("initialDeparture", firstSegment.getDeparture().getAt());
            itineraryMap.put("finalArrival", lastSegment.getArrival().getAt());
            itineraryMap.put("departureAirportCode", firstSegment.getDeparture().getIataCode());
            itineraryMap.put("departureAirportName", getAirportName(firstSegment.getDeparture().getIataCode()));
            itineraryMap.put("arrivalAirportCode", lastSegment.getArrival().getIataCode());
            itineraryMap.put("arrivalAirportName", getAirportName(lastSegment.getArrival().getIataCode()));
            itineraryMap.put("totalFlightTime", durationUtils.formatIsoStringToReadable(itinerary.getDuration()));

            List<Map<String, Object>> segmentDetails = new ArrayList<>();
            for (int i = 0; i < segments.size(); i++) {
                Segment seg = segments.get(i);
                Map<String, Object> segDetail = new LinkedHashMap<>();
                segDetail.put("departureTime", seg.getDeparture().getAt());
                segDetail.put("arrivalTime", seg.getArrival().getAt());
                String mainAirlineCode = seg.getCarrierCode();
                String mainAirlineName = resolveAirlineName(mainAirlineCode, dictionaries);
                segDetail.put("airlineCode", mainAirlineCode);
                segDetail.put("airlineName", mainAirlineName);
                segDetail.put("flightNumber", seg.getNumber());

                if (seg.getOperating() != null && seg.getOperating().getCarrierCode() != null) {
                    String operatingCode = seg.getOperating().getCarrierCode();
                    if (!operatingCode.equals(mainAirlineCode)) {
                        segDetail.put("operatingAirlineCode", operatingCode);
                        segDetail.put("operatingAirlineName", resolveAirlineName(operatingCode, dictionaries));
                    }
                }
                segDetail.put("aircraftType", resolveAircraftName(seg.getAircraft().getCode(), dictionaries));

                List<FareDetailsBySegment> thisSegmentFares =
                        fareDetailsMap.getOrDefault(String.valueOf(seg.getId()), Collections.emptyList());
                List<Map<String, Object>> travelerFaresList = new ArrayList<>();
                for (FareDetailsBySegment fareSeg : thisSegmentFares) {
                    Map<String, Object> fareMap = new LinkedHashMap<>();
                    fareMap.put("cabin", fareSeg.getCabin());
                    fareMap.put("class", fareSeg.getClassName());
                    fareMap.put("amenities", buildAmenitiesList(fareSeg.getAmenities()));
                    travelerFaresList.add(fareMap);
                }
                segDetail.put("travelerFares", travelerFaresList);

                if (i < segments.size() - 1) {
                    Segment nextSeg = segments.get(i + 1);
                    Duration layover = durationUtils.calculateDurationBetween(
                            seg.getArrival().getAt(),
                            nextSeg.getDeparture().getAt()
                    );
                    segDetail.put("layoverTime", durationUtils.formatDurationToReadable(layover));
                }
                segmentDetails.add(segDetail);
            }
            itineraryMap.put("segments", segmentDetails);
            result.add(itineraryMap);
        }
        return result;
    }
}