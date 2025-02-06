package com.flightsearch.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightsearch.backend.model.CurrencyType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SearchController.class)
class SearchTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SearchService searchService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("1) Llama GetAllFlights y recibe una respuesta esperada (Autenticado)")
    @WithMockUser
    void testGetAllFlights_Authenticated() throws Exception {

        List<Map<String, Object>> mockFlightList = List.of(
                Map.of("id", "ABC123", "totalPrice", 100),
                Map.of("id", "XYZ789", "totalPrice", 200)
        );

        given(searchService.getFlightOptions(
                anyString(), // departureAirportKeyword
                anyBoolean(),// isDepartureCode
                anyString(), // arrivalAirportKeyword
                anyBoolean(),// isArrivalCode
                anyString(), // departureDate
                anyString(), // arrivalDate
                anyInt(),    // numAdults
                any(CurrencyType.class), // currency
                anyBoolean(),// nonStop
                anyString(), // sortBy
                anyString(), // order
                anyInt(),    // page
                anyInt()     // size
        )).willReturn(mockFlightList);

        MvcResult result = mockMvc.perform(get("/api/v1/flights")
                                .param("departureAirportKeyword", "MEX")
                                .param("isDepartureCode", "true")
                                .param("arrivalAirportKeyword", "CAN")
                                .param("isArrivalCode", "true")
                                .param("departureDate", "2025-02-05")
                                .param("arrivalDate", "")
                                .param("numAdults", "1")
                                .param("currency", "MXN")
                                .param("nonStop", "true")
                )
                .andReturn();

        System.out.println(result.getResponse().getStatus());
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("2) Llama GetAllFlights sin autentificarte, retorna 401/403")
    void testGetAllFlights_NotAuthenticated() throws Exception {

        mockMvc.perform(get("/api/v1/flights")
                        .param("departureAirportKeyword", "MAD")
                        .param("arrivalAirportKeyword", "BCN")
                        .param("departureDate", "2025-12-01")
                        .param("numAdults", "1")
                        .param("currency", "USD")
                        .param("nonStop", "true")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("3) Llama GetDetailedFlight y recibe una respuesta esperada (Autenticado)")
    @WithMockUser
    void testGetDetailedFlight_Authenticated() throws Exception {

        Map<String, Object> mockDetailedFlight = Map.of(
                "id", "ABC123",
                "initialDeparture", "2025-12-01T08:00:00",
                "finalArrival", "2025-12-01T12:00:00"
        );

        given(searchService.getDetailedFlightOption("ABC123"))
                .willReturn(mockDetailedFlight);

        mockMvc.perform(get("/api/v1/flights/{id}", "ABC123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("ABC123"))
                .andExpect(jsonPath("$.initialDeparture").value("2025-12-01T08:00:00"))
                .andExpect(jsonPath("$.finalArrival").value("2025-12-01T12:00:00"));
    }

    @Test
    @DisplayName("4) Llama GetDetailedFlight sin autentificarte, retorna 401/403")
    void testGetDetailedFlight_NotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/flights/{id}", "ABC123"))
                .andExpect(status().isUnauthorized());
    }

}
