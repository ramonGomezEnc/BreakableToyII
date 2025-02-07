package com.flightsearch.backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flightsearch.backend.client.AmadeusClient;
import com.flightsearch.backend.mapper.FlightOfferDetailMapper;
import com.flightsearch.backend.mapper.FlightOfferMapper;
import com.flightsearch.backend.model.CurrencyType;
import com.flightsearch.backend.model.flightoptions.Dictionaries;
import com.flightsearch.backend.model.flightoptions.FlightOffer;
import com.flightsearch.backend.model.flightoptions.GeneralResponse;
import com.flightsearch.backend.utils.PaginationUtils;
import com.flightsearch.backend.utils.SortingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SearchService {

    private final AmadeusClient amadeusFlightClient;
    private final FlightOfferMapper flightOfferMapper;
    private final FlightOfferDetailMapper flightOfferDetailMapper;
    private final SortingUtils sortingUtils;
    private final PaginationUtils paginationUtils;
    private final Map<String, List<FlightOffer>> flightOffersCache = new HashMap<>();
    private final Map<String, Dictionaries> dictionariesCache = new HashMap<>();
    private final Map<String, List<Map<String, Object>>> mappedFlightsCache = new HashMap<>();
    private final Map<String, Integer> cacheCountByKey = new HashMap<>();
    private String cacheKey = "";

    /**
     * SearchService coordinates calls to the AmadeusClient,
     * performs caching, sorting, and pagination,
     * and delegates mapping to the mappers.
     *
     * @param amadeusFlightClient AmadeusClient for API calls
     * @param flightOfferMapper mapper for essential flight data
     * @param flightOfferDetailMapper mapper for detailed flight data
     * @param sortingUtils utility for flight sorting
     * @param paginationUtils utility for pagination
     */
    @Autowired
    public SearchService(
            AmadeusClient amadeusFlightClient,
            FlightOfferMapper flightOfferMapper,
            FlightOfferDetailMapper flightOfferDetailMapper,
            SortingUtils sortingUtils,
            PaginationUtils paginationUtils
    ) {
        this.amadeusFlightClient = amadeusFlightClient;
        this.flightOfferMapper = flightOfferMapper;
        this.flightOfferDetailMapper = flightOfferDetailMapper;
        this.sortingUtils = sortingUtils;
        this.paginationUtils = paginationUtils;
    }

    private String buildCacheKey(
            String departureAirportKeyword, Boolean isDepartureCode,
            String arrivalAirportKeyword, Boolean isArrivalCode,
            String departureDate, String arrivalDate,
            int numAdults, CurrencyType currency, boolean nonStop
    ) {
        return departureAirportKeyword + "_" + isDepartureCode + "_"
                + arrivalAirportKeyword + "_" + isArrivalCode + "_"
                + departureDate + "_" + arrivalDate + "_"
                + numAdults + "_" + currency + "_" + nonStop;
    }

    /**
     * Retrieves flight offers in a summarized (essential) format, supports sorting and pagination,
     * and caches results for performance. If arrivalDate is provided, it fetches round-trip flights.
     *
     * @return a map containing flight data and a "counter" of total results.
     */
    public Map<String, Object> getFlightOptions(
            String departureAirportKeyword,
            Boolean isDepartureCode,
            String arrivalAirportKeyword,
            Boolean isArrivalCode,
            String departureDate,
            String arrivalDate,
            int numAdults,
            CurrencyType currency,
            boolean nonStop,
            String sortBy,
            String order,
            int page,
            int size
    ) throws JsonProcessingException {
        cacheKey = buildCacheKey(
                departureAirportKeyword, isDepartureCode,
                arrivalAirportKeyword, isArrivalCode,
                departureDate, arrivalDate,
                numAdults, currency, nonStop
        );

        if (!mappedFlightsCache.containsKey(cacheKey)) {
            if (!flightOffersCache.containsKey(cacheKey) || !dictionariesCache.containsKey(cacheKey)) {
                String departureAirportCode = isDepartureCode
                        ? departureAirportKeyword
                        : amadeusFlightClient.fetchAirport(departureAirportKeyword).getIataCode();

                String arrivalAirportCode = isArrivalCode
                        ? arrivalAirportKeyword
                        : amadeusFlightClient.fetchAirport(arrivalAirportKeyword).getIataCode();

                GeneralResponse amadeusResponse = amadeusFlightClient.fetchFlightData(
                        departureAirportCode,
                        arrivalAirportCode,
                        departureDate,
                        arrivalDate,
                        numAdults,
                        currency.name(),
                        nonStop
                );

                flightOffersCache.put(cacheKey, amadeusResponse.getData());
                dictionariesCache.put(cacheKey, amadeusResponse.getDictionaries());
                cacheCountByKey.put(cacheKey, amadeusResponse.getMeta().getCount());
            }
            List<FlightOffer> flightList = flightOffersCache.get(cacheKey);
            Dictionaries dictionaries = dictionariesCache.get(cacheKey);
            List<Map<String, Object>> mappedFlights =
                    flightOfferMapper.buildEssentialFlightList(flightList, dictionaries);
            mappedFlightsCache.put(cacheKey, mappedFlights);
        }

        List<Map<String, Object>> mappedFlights = new ArrayList<>(mappedFlightsCache.get(cacheKey));
        int totalCount = cacheCountByKey.get(cacheKey);
        sortingUtils.applySorting(mappedFlights, sortBy, order);
        List<Map<String, Object>> paginatedList = paginationUtils.applyPagination(mappedFlights, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("counter", totalCount);
        response.put("data", paginatedList);
        return response;
    }

    /**
     * Retrieves a single flight offer in a detailed format from cached data,
     * identified by the flightOfferId.
     *
     * @param flightOfferId the ID of the flight offer to fetch
     * @return a map with detailed flight information, or null if not found
     */
    public Map<String, Object> getDetailedFlightOption(String flightOfferId) throws JsonProcessingException {
        if (!flightOffersCache.containsKey(cacheKey) || !dictionariesCache.containsKey(cacheKey)) {
            return null;
        }
        List<FlightOffer> offers = flightOffersCache.get(cacheKey);
        Dictionaries dictionaries = dictionariesCache.get(cacheKey);

        Optional<FlightOffer> maybeOffer = offers.stream()
                .filter(o -> String.valueOf(o.getId()).equals(flightOfferId))
                .findFirst();
        if (maybeOffer.isEmpty()) {
            return null;
        }
        return flightOfferDetailMapper.buildDetailedFlightOption(maybeOffer.get(), dictionaries);
    }
}
