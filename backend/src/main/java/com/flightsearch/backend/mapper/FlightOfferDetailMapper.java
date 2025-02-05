package com.flightsearch.backend.mapper;

import com.flightsearch.backend.client.AmadeusClient;
import com.flightsearch.backend.model.Airport;
import com.flightsearch.backend.model.flightoptions.Dictionaries;
import com.flightsearch.backend.model.flightoptions.FlightOffer;
import com.flightsearch.backend.model.flightoptions.Segment;
import com.flightsearch.backend.model.flightoptions.TravelerPricing;
import com.flightsearch.backend.model.flightoptions.TravelerPricing.FareDetailsBySegment;
import com.flightsearch.backend.utils.DurationUtils;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;


@Component
public class FlightOfferDetailMapper {

    private final DurationUtils durationUtils;
    private final AmadeusClient amadeusClient;
    private final Map<String, String> airportCache = new HashMap<>();

    public FlightOfferDetailMapper(DurationUtils durationUtils, AmadeusClient amadeusClient) {
        this.durationUtils = durationUtils;
        this.amadeusClient = amadeusClient;
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


    public Map<String, Object> buildDetailedFlightOption(FlightOffer offer, Dictionaries dictionaries) {
        Map<String, Object> detailMap = new LinkedHashMap<>();

        detailMap.put("id", offer.getId());

        if (offer.getItineraries() == null || offer.getItineraries().isEmpty()) {
            return detailMap;
        }

        var itinerary = offer.getItineraries().getFirst();
        var segments = itinerary.getSegments();
        if (segments == null || segments.isEmpty()) {
            return detailMap;
        }

        var firstSegment = segments.getFirst();
        var lastSegment = segments.getLast();

        detailMap.put("initialDeparture", firstSegment.getDeparture().getAt());
        detailMap.put("finalArrival", lastSegment.getArrival().getAt());

        String departureAirportCode = firstSegment.getDeparture().getIataCode();
        String arrivalAirportCode = lastSegment.getArrival().getIataCode();
        detailMap.put("departureAirportCode", departureAirportCode);
        detailMap.put("departureAirportName", getAirportName(departureAirportCode));
        detailMap.put("arrivalAirportCode", arrivalAirportCode);
        detailMap.put("arrivalAirportName", getAirportName(arrivalAirportCode));

        detailMap.put("totalFlightTime", durationUtils.formatIsoStringToReadable(itinerary.getDuration()));

        List<Map<String, Object>> segmentsDetail = new ArrayList<>();
        Map<String, List<FareDetailsBySegment>> fareDetailsMap = buildFareDetailsMap(offer);

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

            String aircraftCode = seg.getAircraft().getCode();
            String aircraftName = resolveAircraftName(aircraftCode, dictionaries);
            segDetail.put("aircraftType", aircraftName);

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

            segmentsDetail.add(segDetail);
        }
        detailMap.put("segments", segmentsDetail);
        detailMap.put("priceBreakdown", buildPriceBreakdown(offer));

        return detailMap;
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

}
