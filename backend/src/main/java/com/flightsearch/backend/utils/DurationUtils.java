package com.flightsearch.backend.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DurationUtils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Calculates the Duration between two date-times using the format yyyy-MM-dd'T'HH:mm:ss.
     *
     * @param startDateTime start date-time
     * @param endDateTime end date-time
     * @return Duration between the two
     */
    public Duration calculateDurationBetween(String startDateTime, String endDateTime) {
        LocalDateTime start = LocalDateTime.parse(startDateTime, DATE_TIME_FORMATTER);
        LocalDateTime end = LocalDateTime.parse(endDateTime, DATE_TIME_FORMATTER);
        return Duration.between(start, end);
    }

    /**
     * Formats a Duration object into a string with the pattern "Xh XXm".
     *
     * @param duration Duration object
     * @return readable string format
     */
    public String formatDurationToReadable(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        return String.format("%dh %02dm", hours, minutes);
    }

    /**
     * Parses an ISO-8601 duration string (e.g. "PT4H40M") and formats it into "Xh XXm".
     *
     * @param isoDurationString an ISO 8601 duration
     * @return formatted duration string
     */
    public String formatIsoStringToReadable(String isoDurationString) {
        Duration duration = Duration.parse(isoDurationString);
        return formatDurationToReadable(duration);
    }
}
