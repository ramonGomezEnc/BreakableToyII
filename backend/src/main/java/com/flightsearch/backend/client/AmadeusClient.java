package com.flightsearch.backend.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightsearch.backend.model.Airport;
import com.flightsearch.backend.model.flightoptions.GeneralResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class AmadeusClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String clientSecret;
    private String clientKey;
    private final ObjectMapper objectMapper;

    /**
     * AmadeusClient is responsible for making REST calls to Amadeus APIs
     * to fetch airport and flight data.
     *
     * @param restTemplate injected RestTemplate
     * @param baseUrl the base URL for the Amadeus API
     * @param clientKey the initial client key (API Key)
     * @param clientSecret the client secret (API Secret)
     */
    public AmadeusClient(
            RestTemplate restTemplate,
            @Value("${api.base_url}") String baseUrl,
            @Value("${api.client_key}") String clientKey,
            @Value("${api.client_secret}") String clientSecret
    ) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.clientKey = clientKey;
        this.clientSecret = clientSecret;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Builds an HttpEntity with the current bearer token.
     *
     * @return HttpEntity with bearer authorization header
     */
    public HttpEntity<String> buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(clientKey);
        return new HttpEntity<>(headers);
    }

    /**
     * Refreshes the OAuth2 token by calling the Amadeus security endpoint,
     * then updates the internal clientKey with the new token.
     */
    private synchronized void refreshToken() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "client_credentials");
            body.add("client_id", clientKey);
            body.add("client_secret", clientSecret);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<JsonNode> response =
                    restTemplate.postForEntity(baseUrl + "/v1/security/oauth2/token", request, JsonNode.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Error when calling Amadeus API: " + response.getStatusCode());
            }
            this.clientKey = response.getBody().path("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Fetches the first matching Airport data based on a keyword or IATA code.
     *
     * @param airportKeyword the airport keyword or partial name
     * @return Airport object containing name and IATA code
     */
    public Airport fetchAirport(String airportKeyword) {
        HttpEntity<String> entity = buildHeaders();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl + "v1/reference-data/locations")
                .queryParam("subType", "AIRPORT")
                .queryParam("keyword", airportKeyword)
                .queryParam("view", "LIGHT");
        String uri = uriBuilder.toUriString();
        ResponseEntity<JsonNode> response;

        try {
            response = restTemplate.exchange(uri, HttpMethod.GET, entity, JsonNode.class);
        } catch (HttpClientErrorException.Unauthorized e) {
            refreshToken();
            return fetchAirport(airportKeyword);
        }

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Error when calling Amadeus API: " + response.getStatusCode());
        }

        JsonNode dataArray = response.getBody().path("data");
        if (!dataArray.isArray() || dataArray.isEmpty()) {
            throw new RuntimeException("No airport data returned for keyword: " + airportKeyword);
        }

        JsonNode firstAirportNode = dataArray.get(0);
        Airport airport = new Airport();
        airport.setName(firstAirportNode.path("name").asText());
        airport.setIataCode(firstAirportNode.path("iataCode").asText());
        return airport;
    }

    /**
     * Fetches flight data (one-way or round-trip) from the Amadeus API.
     * If arrivalDate is not empty, round-trip data is retrieved.
     *
     * @param departureAirportCode the origin IATA code
     * @param arrivalAirportCode the destination IATA code
     * @param departureDate date of departure
     * @param arrivalDate date of return (if round-trip)
     * @param numAdults number of adult travelers
     * @param currency currency code
     * @param nonStop whether to filter only non-stop flights
     * @return GeneralResponse containing flight offers and dictionaries
     * @throws JsonProcessingException if JSON parsing fails
     */
    public GeneralResponse fetchFlightData(
            String departureAirportCode,
            String arrivalAirportCode,
            String departureDate,
            String arrivalDate,
            int numAdults,
            String currency,
            boolean nonStop
    ) throws JsonProcessingException {
        HttpEntity<String> entity = buildHeaders();
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
        ResponseEntity<String> response;

        try {
            response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        } catch (HttpClientErrorException.Unauthorized e) {
            refreshToken();
            return fetchFlightData(
                    departureAirportCode,
                    arrivalAirportCode,
                    departureDate,
                    arrivalDate,
                    numAdults,
                    currency,
                    nonStop
            );
        }

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Error when calling Amadeus API: " + response.getStatusCode());
        }

        String modifiedJson = response.getBody().replace("\"class\":", "\"className\":");
        return objectMapper.readValue(modifiedJson, GeneralResponse.class);
    }
}
