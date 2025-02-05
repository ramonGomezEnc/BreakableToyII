package com.flightsearch.backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flightsearch.backend.model.CurrencyType;
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
            @RequestParam String departureAirportKeyword,
            @RequestParam(defaultValue = "true") Boolean isDepartureCode,
            @RequestParam String arrivalAirportKeyword,
            @RequestParam(defaultValue = "true") Boolean isArrivalCode,
            @RequestParam String departureDate,
            @RequestParam(defaultValue = "") String arrivalDate,
            @RequestParam int numAdults,
            @RequestParam CurrencyType currency,
            @RequestParam boolean nonStop,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String order, //ASC, DES
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        try {
            List<Map<String, Object>> flights = service.getFlightOptions(
                    departureAirportKeyword,
                    isDepartureCode,
                    arrivalAirportKeyword,
                    isArrivalCode,
                    departureDate,
                    arrivalDate,
                    numAdults,
                    currency,
                    nonStop,
                    sortBy,
                    order,
                    page,
                    size
            );
            return ResponseEntity.ok(flights);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
