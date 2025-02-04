package com.flightsearch.backend.flight;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightsearch.backend.flight.model.GeneralResponse;
import com.flightsearch.backend.flight.model.Dictionaries;
import com.flightsearch.backend.flight.model.FlightOffer;
import com.flightsearch.backend.flight.model.CurrencyType;
import com.flightsearch.backend.flight.utils.TimeFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.*;

@Service
public class SearchService {
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String clientKey;
    private final Map<String, FlightOffer> flightOffers = new HashMap<>();
    private Dictionaries dictionaries;
    @Autowired
    private TimeFunctions timeFunctions;


    public SearchService(
            RestTemplate restTemplate,
            @Value("${api.base_url}") String baseUrl,
            @Value("${api.client_key}") String clientKey
    ) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.clientKey = clientKey;
    }

    private List<Map<String, Object>> buildEssentialFlightList(List<FlightOffer> flightOffers) {
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

            var itinerary = offer.getItineraries().getFirst();
            var firstSegment = itinerary.getSegments().getFirst();
            var lastSegment = itinerary.getSegments().getLast();

            flightMap.put("initialDeparture", firstSegment.getDeparture().getAt());
            flightMap.put("finalArrival", lastSegment.getArrival().getAt());

            flightMap.put("departureAirportCode", firstSegment.getDeparture().getIataCode());
            flightMap.put("arrivalAirportCode", lastSegment.getArrival().getIataCode());

            var mainAirlineCode = firstSegment.getCarrierCode();
            String mainAirlineName = (dictionaries != null && dictionaries.getCarriers() != null)
                    ? dictionaries.getCarriers().getOrDefault(mainAirlineCode, mainAirlineCode)
                    : mainAirlineCode;
            flightMap.put("airlineCode", mainAirlineCode);
            flightMap.put("airlineName", mainAirlineName);

            if (firstSegment.getOperating() != null) {
                var operatingAirlineCode = firstSegment.getOperating().getCarrierCode();
                if (operatingAirlineCode != null && !operatingAirlineCode.equals(mainAirlineCode)) {
                    flightMap.put("operatingAirlineCode", operatingAirlineCode);
                    if (dictionaries != null && dictionaries.getCarriers() != null) {
                        flightMap.put("operatingAirlineName",
                                dictionaries.getCarriers().getOrDefault(operatingAirlineCode, operatingAirlineCode));
                    }
                }
            }


            Duration totalDuration = timeFunctions.calculateTotalDuration(
                    firstSegment.getDeparture().getAt(),
                    lastSegment.getArrival().getAt()
            );
            flightMap.put("totalFlightTime", timeFunctions.formatDuration(totalDuration));

            List<Map<String, Object>> stops = new ArrayList<>();
            List<com.flightsearch.backend.flight.model.Segment> segments = itinerary.getSegments();
            for (int i = 0; i < segments.size() - 1; i++) {
                var segA = segments.get(i);
                var segB = segments.get(i + 1);
                Duration layover = timeFunctions.calculateTotalDuration(
                        segA.getArrival().getAt(),
                        segB.getDeparture().getAt()
                );
                Map<String, Object> stopInfo = new LinkedHashMap<>();
                stopInfo.put("airportCode", segA.getArrival().getIataCode());
                stopInfo.put("layoverTime", timeFunctions.formatDuration(layover));
                stops.add(stopInfo);
            }
            if (!stops.isEmpty()) {
                flightMap.put("stops", stops);
            }

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

    public List<Map<String, Object>> getFlightOptions(
            String departureAirportCode,
            String arrivalAirportCode,
            String departureDate,
            String arrivalDate,
            int numAdults,
            CurrencyType currency,
            boolean nonStop
    ) throws JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(clientKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl + "v2/shopping/flight-offers")
                .queryParam("originLocationCode", departureAirportCode)
                .queryParam("destinationLocationCode", arrivalAirportCode)
                .queryParam("departureDate", departureDate)
                .queryParam("adults", numAdults)
                .queryParam("currencyCode", currency.name())
                .queryParam("nonStop", nonStop);
        if (arrivalDate != null && !arrivalDate.isEmpty()) {
            uriBuilder.queryParam("returnDate", arrivalDate);
        }

        String uri = uriBuilder.toUriString();
        ResponseEntity<JsonNode> response = restTemplate.exchange(uri, HttpMethod.GET, entity, JsonNode.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Error when calling Amadeus API: " + response.getStatusCode());
        }

        ObjectMapper mapper = new ObjectMapper();
        GeneralResponse amadeusResponse = mapper.readValue(response.getBody().toString(), GeneralResponse.class);

        this.flightOffers.clear();
        if (amadeusResponse.getData() != null) {
            for (FlightOffer fo : amadeusResponse.getData()) {
                this.flightOffers.put(fo.getId(), fo);
            }
        }
        this.dictionaries = amadeusResponse.getDictionaries();

        return this.buildEssentialFlightList(amadeusResponse.getData());
    }
}