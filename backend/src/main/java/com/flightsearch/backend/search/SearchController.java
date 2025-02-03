package com.flightsearch.backend.search;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class SearchController {
    private final SearchService service;

    @Autowired
    public SearchController(SearchService service) {
        this.service = service;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

    @GetMapping("/search")
    public ResponseEntity<JsonNode> getAllFlightOptions(
            @RequestParam(required = true) String departureAirportCode,
            @RequestParam(required = true) String arrivalAirportCode,
            @RequestParam(required = true) String departureDate,
            @RequestParam(defaultValue = "") String arrivalDate,
            @RequestParam(required = true) int numAdults,
            @RequestParam(required = true) CurrencyType currency,
            @RequestParam(required = true) boolean nonStop
    ) {

        try {
            return new ResponseEntity<>(service.getFlightOptions(
                    departureAirportCode,
                    arrivalAirportCode,
                    departureDate,
                    arrivalDate,
                    numAdults,
                    currency,
                    nonStop
            ), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
