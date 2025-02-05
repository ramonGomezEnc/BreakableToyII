package com.flightsearch.backend.utils;

import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SortingUtils {

    private Duration parseDuration(Object totalFlightTimeObj) {
        String raw = totalFlightTimeObj.toString().trim();
        Pattern pattern = Pattern.compile("^(\\d+)h\\s*(\\d+)m$");
        Matcher matcher = pattern.matcher(raw);
        if (matcher.matches()) {
            long hours = Long.parseLong(matcher.group(1));
            long minutes = Long.parseLong(matcher.group(2));
            return Duration.ofMinutes(hours * 60 + minutes);
        }
        return null;
    }

    public void applySorting(List<Map<String, Object>> flights, String sortBy, String order) {
        if (sortBy == null || sortBy.isEmpty()) {
            return;
        }

        Comparator<Map<String, Object>> comparator;
        switch (sortBy.toLowerCase()) {
            case "price":
                comparator = Comparator.comparing(f -> {
                    Object price = f.get("totalPrice");
                    return (price != null) ? Double.parseDouble(price.toString()) : Double.MAX_VALUE;
                });
                break;

            case "duration":
                comparator = Comparator.comparing(f -> parseDuration(f.get("totalFlightTime")),
                        Comparator.nullsLast(Comparator.naturalOrder()));
                break;

            default:
                return;
        }

        flights.sort(comparator);

        if ("desc".equalsIgnoreCase(order)) {
            Collections.reverse(flights);
        }
    }
}
