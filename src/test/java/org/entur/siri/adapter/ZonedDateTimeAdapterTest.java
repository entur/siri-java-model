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

import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

import static org.junit.Assert.*;

public class ZonedDateTimeAdapterTest {

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
    public void testParseTimestamps() {
        ZonedDateTime utcTime = ZonedDateTimeAdapter.parse("1690419600");
        assertEquals(2023, utcTime.getYear());
        assertEquals(Month.JULY, utcTime.getMonth());
        assertEquals(27, utcTime.getDayOfMonth());

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