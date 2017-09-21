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