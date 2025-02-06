package com.flightsearch.backend.model.flightoptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Price {
    private String currency;
    private String total;
    private String base;
    private List<Fee> fees;
    private String grandTotal;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Fee {
        private String amount;
        private String type;
    }
}
