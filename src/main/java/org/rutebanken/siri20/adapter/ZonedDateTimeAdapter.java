package org.rutebanken.siri20.adapter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ZonedDateTimeAdapter {

    /**
     * Parses dateTime to ZonedDateTime with optional zone
     *
     * If Zone is not provided, local system default is used
     *
     * @param dateTime
     * @return
     */
    public static ZonedDateTime parse(String dateTime) {
        ZonedDateTime parsed;
        try {
            parsed = ZonedDateTime.parse(dateTime);
        } catch (DateTimeParseException e) {
            LocalDateTime parse1 = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            parsed =ZonedDateTime.ofLocal(parse1, ZoneId.systemDefault(), ZoneOffset.ofHours(0));

        }
        return parsed.withZoneSameInstant(ZoneId.systemDefault());
    }
}
