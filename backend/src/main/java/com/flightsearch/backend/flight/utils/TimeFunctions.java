package com.flightsearch.backend.flight.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeFunctions {
    public Duration calculateTotalDuration(String start, String end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);
        return Duration.between(startTime, endTime);
    }

    public String formatDuration(Duration d) {
        long hours = d.toHours();
        long minutes = d.toMinutes() % 60;
        return String.format("%dh %02dm", hours, minutes);
    }
}
