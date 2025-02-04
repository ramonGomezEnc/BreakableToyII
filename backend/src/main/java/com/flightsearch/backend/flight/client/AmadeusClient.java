package com.flightsearch.backend.flight.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightsearch.backend.flight.model.GeneralResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class AmadeusClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String clientKey;
    private final ObjectMapper objectMapper;

    public AmadeusClient(
            RestTemplate restTemplate,
            @Value("${api.base_url}") String baseUrl,
            @Value("${api.client_key}") String clientKey
    ) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.clientKey = clientKey;
        this.objectMapper = new ObjectMapper();
    }


    public GeneralResponse fetchFlightData(
            String departureAirportCode,
            String arrivalAirportCode,
            String departureDate,
            String arrivalDate,
            int numAdults,
            String currency,
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
                .queryParam("currencyCode", currency)
                .queryParam("nonStop", nonStop);

        if (arrivalDate != null && !arrivalDate.isEmpty()) {
            uriBuilder.queryParam("returnDate", arrivalDate);
        }

        String uri = uriBuilder.toUriString();
        ResponseEntity<JsonNode> response = restTemplate.exchange(uri, HttpMethod.GET, entity, JsonNode.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Error when calling Amadeus API: " + response.getStatusCode());
        }

        return objectMapper.readValue(response.getBody().toString(), GeneralResponse.class);
    }
}
