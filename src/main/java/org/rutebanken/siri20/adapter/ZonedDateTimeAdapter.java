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
        return str.matches("\\d+");
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
