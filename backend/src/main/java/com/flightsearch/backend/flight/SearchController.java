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

    /**
     * Endpoint GET: /flights
     * Return a flight options list (only essential information)
     */
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

    /**
     * Endpoint GET: /flights/{id}
     * Return a detailed flight option by id
     */
//    @GetMapping("/flights/{id}")
//    public ResponseEntity<Map<String, Object>> getFlightOptionsById(@PathVariable String id) {
//
//        try {
//            Map<String, Object> flight = service.getFlightOptionById(id);
//            return ResponseEntity.ok(flight);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
}
