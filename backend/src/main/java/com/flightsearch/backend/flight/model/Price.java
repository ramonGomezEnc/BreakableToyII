package com.flightsearch.backend.flight.model;

import lombok.Data;
import java.util.List;

@Data
public class Price {
    private String currency;
    private String total;
    private String base;
    private List<Fee> fees;
    private String grandTotal;

    @Data
    public static class Fee {
        private String amount;
        private String type;
    }
}
