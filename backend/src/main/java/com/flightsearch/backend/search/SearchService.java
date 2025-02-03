package com.flightsearch.backend.search;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Service
public class SearchService {
    @Autowired
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String clientKey;

    public SearchService(
            RestTemplate restTemplate,
            @Value("${api.base_url}") String baseUrl,
            @Value("${api.client_key}") String clientKey
    ) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.clientKey = clientKey;
    }

    public JsonNode getFlightOptions(
            String departureAirportCode,
            String arrivalAirportCode,
            String departureDate,
            String arrivalDate,
            int numAdults,
            CurrencyType currency,
            boolean nonStop
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(clientKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl + "v2/shopping/flight-offers")
                .queryParam("originLocationCode", departureAirportCode)
                .queryParam("destinationLocationCode", arrivalAirportCode)
                .queryParam("departureDate", departureDate)
                .queryParam("adults", numAdults)
                .queryParam("currencyCode", currency)
                .queryParam("nonStop", nonStop);

        if (arrivalDate != null && !arrivalDate.isEmpty()) {
            uriBuilder.queryParam("returnDate", arrivalDate);
        }

        URI uri = uriBuilder.build().toUri();
        ResponseEntity<JsonNode> response = restTemplate.exchange(uri, HttpMethod.GET, entity, JsonNode.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to fetch flight data: " + response.getStatusCode());
        }
    }
}
