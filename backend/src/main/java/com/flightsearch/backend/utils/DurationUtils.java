package com.flightsearch.backend.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class DurationUtils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public Duration calculateDurationBetween(String startDateTime, String endDateTime) {
        LocalDateTime start = LocalDateTime.parse(startDateTime, DATE_TIME_FORMATTER);
        LocalDateTime end = LocalDateTime.parse(endDateTime, DATE_TIME_FORMATTER);
        return Duration.between(start, end);
    }

    public String formatDurationToReadable(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        return String.format("%dh %02dm", hours, minutes);
    }

    public String formatIsoStringToReadable(String isoDurationString) {
        Duration duration = Duration.parse(isoDurationString);
        return formatDurationToReadable(duration);
    }
}
