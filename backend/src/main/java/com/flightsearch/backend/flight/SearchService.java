package com.flightsearch.backend.flight;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flightsearch.backend.flight.client.AmadeusClient;
import com.flightsearch.backend.flight.mapper.FlightOfferMapper;
import com.flightsearch.backend.flight.model.CurrencyType;
import com.flightsearch.backend.flight.model.Dictionaries;
import com.flightsearch.backend.flight.model.FlightOffer;
import com.flightsearch.backend.flight.model.GeneralResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SearchService {

    private final AmadeusClient amadeusFlightClient;
    private final FlightOfferMapper flightOfferMapper;
    private Dictionaries dictionaries;
    private List<FlightOffer> flightList;

    @Autowired
    public SearchService(
            AmadeusClient amadeusFlightClient,
            FlightOfferMapper flightOfferMapper
    ) {
        this.amadeusFlightClient = amadeusFlightClient;
        this.flightOfferMapper = flightOfferMapper;
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

        this.dictionaries = null;
        this.flightList = null;

        GeneralResponse amadeusResponse = amadeusFlightClient.fetchFlightData(
                departureAirportCode,
                arrivalAirportCode,
                departureDate,
                arrivalDate,
                numAdults,
                currency.name(),
                nonStop
        );

        this.flightList = amadeusResponse.getData();
        this.dictionaries = amadeusResponse.getDictionaries();

        return flightOfferMapper.buildEssentialFlightList(
                this.flightList,
                this.dictionaries
        );
    }
}
