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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class ZonedDateTimeAdapter {

    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();
    private static final ZoneOffset ZERO_OFFSET = ZoneOffset.ofHours(0);

    /**
     * Parses dateTime to ZonedDateTime with optional zone.
     * If Zone is not provided, local system default is used.
     * <p>
     * The common form {@code yyyy-MM-ddTHH:mm:ss[.fffffffff][Z|±HH:MM]} is handled by
     * {@link FastZonedDateTimeParser}; everything else goes through the full ISO parser.
     *
     * @param dateTime ISO-formatted string
     */
    public static ZonedDateTime parse(String dateTime) {
        Objects.requireNonNull(dateTime, "dateTime");
        ZonedDateTime fast = FastZonedDateTimeParser.parse(dateTime, DEFAULT_ZONE);
        return fast != null ? fast : parseFull(dateTime);
    }

    private static ZonedDateTime parseFull(String dateTime) {
        ZonedDateTime parsed;
        try {
            parsed = ZonedDateTime.parse(dateTime);
        } catch (DateTimeParseException e) {
            LocalDateTime local = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            parsed = ZonedDateTime.ofLocal(local, DEFAULT_ZONE, ZERO_OFFSET);
        }
        return parsed.withZoneSameInstant(DEFAULT_ZONE);
    }
}
