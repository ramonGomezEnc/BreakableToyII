package com.flightsearch.backend.flight;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flightsearch.backend.flight.model.CurrencyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class SearchController {
    private final SearchService service;

    @Autowired
    public SearchController(SearchService service) {
        this.service = service;
    }

    @GetMapping("/flights")
    public ResponseEntity<List<Map<String, Object>>> getAllFlightOptions(
            @RequestParam String departureAirportCode,
            @RequestParam String arrivalAirportCode,
            @RequestParam String departureDate,
            @RequestParam(defaultValue = "") String arrivalDate,
            @RequestParam int numAdults,
            @RequestParam CurrencyType currency,
            @RequestParam boolean nonStop
    ) {

        try {
            List<Map<String, Object>> flights = service.getFlightOptions(
                    departureAirportCode,
                    arrivalAirportCode,
                    departureDate,
                    arrivalDate,
                    numAdults,
                    currency,
                    nonStop
            );
            return ResponseEntity.ok(flights);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
