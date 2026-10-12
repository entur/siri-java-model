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

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.Assert.*;

public class FastZonedDateTimeParserTest {

    private static final ZoneId OSLO = ZoneId.of("Europe/Oslo");

    @Test
    public void testSupportedFormsMatchFullParser() {
        String[] supported = {
            "2026-09-18T15:46:00+02:00",
            "2026-09-18T15:46:00.5+02:00",
            "2026-09-18T15:46:00.886+02:00",
            "2026-09-18T15:46:00.88692943+02:00",
            "2026-09-18T15:46:00.123456789+02:00",
            "2026-09-18T13:46:00Z",
            "2026-09-18T13:46:00.000Z",
            "2026-09-18T13:46:00-05:00",
            "2026-09-18T13:46:00-00:00",
            "2026-09-18T13:46:00+05:30",
            "2026-09-18T13:46:00+18:00",
            "2026-09-18T13:46:00-18:00",
            "2024-02-29T00:00:00+02:00",
            "2026-03-29T02:30:00+01:00",
            "2026-10-25T02:30:00+02:00",
            "2026-10-25T02:30:00+01:00",
        };
        for (String value : supported) {
            ZonedDateTime expected = ZonedDateTime.parse(value).withZoneSameInstant(OSLO);
            assertEquals(value, expected, FastZonedDateTimeParser.parse(value, OSLO));
        }
    }

    @Test
    public void testResultIsInRequestedZone() {
        assertEquals(OSLO, FastZonedDateTimeParser.parse("2026-09-18T13:46:00Z", OSLO).getZone());
        ZoneId utc = ZoneId.of("UTC");
        assertEquals(utc, FastZonedDateTimeParser.parse("2026-09-18T15:46:00+02:00", utc).getZone());
        assertEquals(13, FastZonedDateTimeParser.parse("2026-09-18T15:46:00+02:00", utc).getHour());
    }

    @Test
    public void testLocalTimeWithoutZoneMatchesAdapterFallback() {
        String[] local = {
            "2026-09-18T13:46:00",
            "2026-09-18T13:46:00.25",
            "2026-09-18T13:46:00.123456789",
            // Europe/Oslo DST gap (does not exist) and overlap (exists twice)
            "2026-03-29T02:30:00",
            "2026-10-25T02:30:00",
        };
        for (String value : local) {
            ZonedDateTime expected = ZonedDateTime.ofLocal(LocalDateTime.parse(value), OSLO, ZoneOffset.UTC);
            assertEquals(value, expected, FastZonedDateTimeParser.parse(value, OSLO));
        }
        assertEquals(OSLO, FastZonedDateTimeParser.parse("2026-09-18T13:46:00", OSLO).getZone());
    }

    @Test
    public void testUnsupportedIsoFormsReturnNull() {
        String[] unsupported = {
            "2026-09-18T13:46:00+02:00:30",
            "2026-09-18T13:46:00+02",
            "2026-09-18T13:46+02:00",
            "2026-09-18T13:46:00.+02:00",
            "2026-09-18T13:46:00+02:00[Europe/Oslo]",
            "2026-09-18T13:46:00Z[UTC]",
            "2026-09-18t13:46:00z",
            "+10000-01-01T00:00:00Z",
            "-0001-01-01T00:00:00Z",
        };
        for (String value : unsupported) {
            assertNull(value, FastZonedDateTimeParser.parse(value, OSLO));
        }
    }

    @Test
    public void testInvalidValuesReturnNull() {
        String[] invalid = {
            "",
            "XXX",
            "2026-09-18",
            "12:58:00+01:00",
            "2026-09-18T13:46:00+19:00",
            "2026-09-18T13:46:00+02:60",
            "2026-09-18T13:46:00+2:00",
            "2026-09-18T13:46:00+0200",
            "2026-09-18T13:46:00.1234567890+02:00",
            "2026-13-18T13:46:00+02:00",
            "2026-02-30T13:46:00+02:00",
            "2023-02-29T00:00:00+02:00",
            "2026-09-18T24:00:00+02:00",
            "2026-09-18T23:60:00+02:00",
            "2026-09-18T23:59:60+02:00",
            "2026-09-18 13:46:00+02:00",
            " 2026-09-18T13:46:00+02:00",
            "2026-09-18T13:46:00+02:00 ",
            "２０２６-09-18T13:46:00+02:00",
            "2026-09-18T13:46:00+０2:00",
        };
        for (String value : invalid) {
            assertNull(value, FastZonedDateTimeParser.parse(value, OSLO));
        }
    }
}
