/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.entur.siri.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class ZonedDateTimeAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZonedDateTimeAdapter.class);

    /**
     * Parses dateTime to ZonedDateTime with optional zone.
     * If Zone is not provided, local system default is used.
     *
     * @param dateTime Maybe either ISO-formatted string, or timestamp in milliseconds
     */
    public static ZonedDateTime parse(String dateTime) {
        Objects.requireNonNull(dateTime, "dateTime");
        ZonedDateTime parsed;
        if (containsOnlyDigits(dateTime)) {
            LOGGER.trace("ZoneDateTime provided as a timestamp: {}", dateTime);
            parsed = parse(Long.parseLong(dateTime));
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

    private static boolean containsOnlyDigits(String str) {
        int length = str.length();
        if(length == 0) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (str.charAt(i) < '0'
                    || str.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }

    /**
     * Parses dateTime to ZonedDateTime with optional zone.
     * If Zone is not provided, local system default is used.
     *
     */
    private static ZonedDateTime parse(long dateTime) {
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(dateTime), ZoneId.systemDefault());
    }
}
