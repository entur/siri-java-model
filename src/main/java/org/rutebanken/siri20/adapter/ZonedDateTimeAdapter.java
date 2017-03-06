package org.rutebanken.siri20.adapter;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ZonedDateTimeAdapter {

    /**
     * Parses dateTime to ZonedDateTime with optional zone
     *
     * If Zone is not provided, local system default is used
     *
     * @param dateTime May be either ISO-formatted string, or timestamp in milliseconds
     * @return
     */
    public static ZonedDateTime parse(String dateTime) {
        ZonedDateTime parsed;
        if (dateTime!= null && isNumeric(dateTime)) {
            parsed = parse(Long.valueOf(dateTime));
        } else {
            try {
                parsed = ZonedDateTime.parse(dateTime);
            } catch (DateTimeParseException e) {
                LocalDateTime parse1 = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                parsed = ZonedDateTime.ofLocal(parse1, ZoneId.systemDefault(), ZoneOffset.ofHours(0));

            }
        }
        return parsed.withZoneSameInstant(ZoneId.systemDefault());
    }

    private static boolean isNumeric(String str)
    {
        return str.matches("\\d+");  //match a number with optional '-' and decimal.
    }

    /**
     * Parses dateTime to ZonedDateTime with optional zone
     *
     * If Zone is not provided, local system default is used
     *
     * @param dateTime
     * @return
     */
    private static ZonedDateTime parse(long dateTime) {
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(dateTime), ZoneId.systemDefault());
    }
}
