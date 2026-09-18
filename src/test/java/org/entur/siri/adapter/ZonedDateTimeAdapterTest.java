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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static org.junit.Assert.*;

public class ZonedDateTimeAdapterTest {

    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();

    /** Values that must parse, in the shapes seen in real SIRI feeds plus ISO variants. */
    private static final String[] VALID = {
        "2026-09-18T15:46:00+02:00",
        "2026-09-18T15:46:00.5+02:00",
        "2026-09-18T15:46:00.88+02:00",
        "2026-09-18T15:46:00.886+02:00",
        "2026-09-18T15:46:00.8869+02:00",
        "2026-09-18T15:46:00.88692+02:00",
        "2026-09-18T15:46:00.886929+02:00",
        "2026-09-18T15:46:00.8869294+02:00",
        "2026-09-18T15:46:00.88692943+02:00",
        "2026-09-18T15:46:00.123456789+02:00",
        "2026-09-18T13:46:00Z",
        "2026-09-18T13:46:00.000Z",
        "2026-09-18T13:46:00-05:00",
        "2026-09-18T13:46:00-00:00",
        "2026-09-18T13:46:00+00:00",
        "2026-09-18T13:46:00+05:30",
        "2026-09-18T13:46:00+18:00",
        "2026-09-18T13:46:00-18:00",
        // ISO forms outside the fast path, handled by the full parser
        "2026-09-18T13:46:00+02:00:30",
        "2026-09-18T13:46:00+02",
        "2026-09-18T13:46+02:00",
        "2026-09-18T13:46:00.+02:00",
        "2026-09-18T13:46:00+02:00[Europe/Oslo]",
        "2026-09-18T13:46:00Z[UTC]",
        "2026-09-18t13:46:00z",
        "+10000-01-01T00:00:00Z",
        "-0001-01-01T00:00:00Z",
        "0000-01-01T00:00:00Z",
        "9999-12-31T23:59:59.999999999Z",
        "2024-02-29T00:00:00+02:00",
        // offset-less local times, interpreted in the system default zone
        "2026-09-18T13:46:00",
        "2026-09-18T13:46:00.25",
        "2026-09-18T13:46",
        // Europe/Oslo DST gap and overlap
        "2026-03-29T02:30:00+01:00",
        "2026-10-25T02:30:00+02:00",
        "2026-10-25T02:30:00+01:00",
    };

    /** Values that must be rejected with DateTimeParseException. */
    private static final String[] INVALID = {
        "XXX",
        "",
        "2026-09-18",
        "12:58:00+01:00",
        "12:58:00",
        "2026-09-18T13:46:00+19:00",
        "2026-09-18T13:46:00+02:60",
        "2026-09-18T13:46:00+2:00",
        "2026-09-18T13:46:00+0200",
        "2026-09-18T13:46:00.1234567890+02:00",
        "2026-09-18T13:46:00[Europe/Oslo]",
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

    /** The original implementation, kept as the behavioural reference for the fast path. */
    private static ZonedDateTime reference(String dateTime) {
        ZonedDateTime parsed;
        try {
            parsed = ZonedDateTime.parse(dateTime);
        } catch (DateTimeParseException e) {
            LocalDateTime local = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            parsed = ZonedDateTime.ofLocal(local, DEFAULT_ZONE, ZoneOffset.UTC);
        }
        return parsed.withZoneSameInstant(DEFAULT_ZONE);
    }

    @Test
    public void testParseDifferentZones() {

        ZonedDateTime utcTime = ZonedDateTimeAdapter.parse("2017-02-24T12:58:00Z");

        ZonedDateTime localZoneTime = ZonedDateTimeAdapter.parse("2017-02-24T13:58:00+01:00");

        ZonedDateTime otherZoneTime = ZonedDateTimeAdapter.parse("2017-02-24T16:58:00+04:00");

        ZoneId zone = ZoneId.of("+01:00");
        assertEquals(2017, localZoneTime.withZoneSameInstant(zone).getYear());
        assertEquals(2, localZoneTime.withZoneSameInstant(zone).getMonthValue());
        assertEquals(24, localZoneTime.withZoneSameInstant(zone).getDayOfMonth());
        assertEquals(13, localZoneTime.withZoneSameInstant(zone).getHour());
        assertEquals(58, localZoneTime.withZoneSameInstant(zone).getMinute());
        assertEquals(0, localZoneTime.withZoneSameInstant(zone).getSecond());

        assertTrue(localZoneTime.isEqual(utcTime));
        assertTrue(localZoneTime.isEqual(otherZoneTime));
    }

    @Test
    public void testResultIsInSystemDefaultZone() {
        for (String value : VALID) {
            assertEquals(value, DEFAULT_ZONE, ZonedDateTimeAdapter.parse(value).getZone());
        }
    }

    @Test
    public void testFractionalSeconds() {
        assertEquals(500_000_000, ZonedDateTimeAdapter.parse("2026-09-18T15:46:00.5+02:00").getNano());
        assertEquals(886_000_000, ZonedDateTimeAdapter.parse("2026-09-18T15:46:00.886+02:00").getNano());
        assertEquals(886_929_430, ZonedDateTimeAdapter.parse("2026-09-18T15:46:00.88692943+02:00").getNano());
        assertEquals(123_456_789, ZonedDateTimeAdapter.parse("2026-09-18T15:46:00.123456789+02:00").getNano());
    }

    @Test
    public void testOffsetIsApplied() {
        ZonedDateTime plusTwo = ZonedDateTimeAdapter.parse("2026-09-18T15:46:00+02:00");
        ZonedDateTime utc = ZonedDateTimeAdapter.parse("2026-09-18T13:46:00Z");
        ZonedDateTime minusFive = ZonedDateTimeAdapter.parse("2026-09-18T08:46:00-05:00");
        ZonedDateTime halfHour = ZonedDateTimeAdapter.parse("2026-09-18T19:16:00+05:30");
        assertEquals(plusTwo.toInstant(), utc.toInstant());
        assertEquals(plusTwo.toInstant(), minusFive.toInstant());
        assertEquals(plusTwo.toInstant(), halfHour.toInstant());
    }

    @Test
    public void testOffsetLessValueUsesSystemDefaultZone() {
        ZonedDateTime local = ZonedDateTimeAdapter.parse("2026-09-18T13:46:00");
        assertEquals(LocalDateTime.of(2026, 9, 18, 13, 46), local.toLocalDateTime());
        assertEquals(DEFAULT_ZONE, local.getZone());
    }

    @Test
    public void testEquivalentToReferenceImplementation() {
        for (String value : VALID) {
            assertEquals(value, reference(value), ZonedDateTimeAdapter.parse(value));
        }
    }

    @Test
    public void testInvalidValuesAreRejected() {
        for (String value : INVALID) {
            assertThrows("'" + value + "'", DateTimeParseException.class, () -> ZonedDateTimeAdapter.parse(value));
        }
    }

    @Test
    public void testParseInvalid() {
        assertThrows(DateTimeParseException.class, ()-> ZonedDateTimeAdapter.parse("XXX"));
    }

    @Test
    public void testParseEmpty() {
        assertThrows(DateTimeParseException.class, ()-> ZonedDateTimeAdapter.parse(""));
    }

    @Test
    public void testParseNull() {
        assertThrows(NullPointerException.class, ()-> ZonedDateTimeAdapter.parse(null));
    }

}
