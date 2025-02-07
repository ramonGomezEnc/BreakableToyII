package com.flightsearch.backend.utils;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SortingUtils {

    /**
     * Applies sorting on a list of flight objects based on either price or duration.
     * If sortBy is not provided, no sorting is applied. Order can be "ASC" or "DES".
     *
     * @param flights list of flight maps
     * @param sortBy "price" or "duration"
     * @param order "ASC" or "DES"
     */
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
                comparator = Comparator.comparing(f -> {
                    Object itineraries = f.get("itineraries");
                    if (!(itineraries instanceof List)) {
                        return Duration.ofDays(999);
                    }
                    Duration total = Duration.ZERO;
                    for (Object obj : (List<?>) itineraries) {
                        if (obj instanceof Map) {
                            Object dur = ((Map<?, ?>) obj).get("totalFlightTime");
                            Duration d = parseDuration(dur);
                            if (d != null) {
                                total = total.plus(d);
                            }
                        }
                    }
                    return total.equals(Duration.ZERO) ? Duration.ofDays(999) : total;
                });
                break;
            default:
                return;
        }
        if ("des".equalsIgnoreCase(order)) {
            flights.sort(comparator.reversed());
        } else {
            flights.sort(comparator);
        }
    }

    private Duration parseDuration(Object totalFlightTimeObj) {
        if (totalFlightTimeObj == null) return null;
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
}
