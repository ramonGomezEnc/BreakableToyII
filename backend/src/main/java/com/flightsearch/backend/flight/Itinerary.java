package com.flightsearch.backend.flight;

import lombok.Getter;
import java.util.List;

@Getter
public class Itinerary {
    private String duration;
    private List<Segment> segments;
}
