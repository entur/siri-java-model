package org.rutebanken.siri20.adapter;

import junit.framework.TestCase;

import java.time.ZonedDateTime;

public class ZonedDateTimeAdapterTest extends TestCase {

    public void testParseDifferentZones() {

        ZonedDateTime utcTime = ZonedDateTimeAdapter.parse("2017-02-24T12:58:00Z");

        ZonedDateTime localZoneTime = ZonedDateTimeAdapter.parse("2017-02-24T13:58:00+01:00");

        ZonedDateTime otherZoneTime = ZonedDateTimeAdapter.parse("2017-02-24T16:58:00+04:00");

        assertEquals(2017, localZoneTime.getYear());
        assertEquals(2, localZoneTime.getMonthValue());
        assertEquals(24, localZoneTime.getDayOfMonth());
        assertEquals(13, localZoneTime.getHour());
        assertEquals(58, localZoneTime.getMinute());
        assertEquals(0, localZoneTime.getSecond());

        assertEquals(localZoneTime, utcTime);
        assertEquals(localZoneTime, otherZoneTime);
    }
}