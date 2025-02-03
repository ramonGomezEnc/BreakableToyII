package com.flightsearch.backend.flight;

import lombok.Getter;
import java.util.List;

@Getter
public class Price {
    private String currency;
    private String total;
    private String base;
    private List<Fee> fees;
    private String grandTotal;

    @Getter
    public static class Fee {
        private String amount;
        private String type;
    }
}
