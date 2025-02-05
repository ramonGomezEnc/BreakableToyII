package com.flightsearch.backend.utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PaginationUtils {

    public List<Map<String, Object>> applyPagination(List<Map<String, Object>> flights, int page, int size) {
        if (page < 0) page = 0;
        if (size <= 0) size = 10;

        int fromIndex = page * size;
        if (fromIndex >= flights.size()) {
            return Collections.emptyList();
        }

        int toIndex = Math.min(fromIndex + size, flights.size());
        return flights.subList(fromIndex, toIndex);
    }
}
