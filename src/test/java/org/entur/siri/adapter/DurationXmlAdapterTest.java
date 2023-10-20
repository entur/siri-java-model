package org.entur.siri.adapter;

import org.junit.Test;

import java.time.Duration;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotSame;

public class DurationXmlAdapterTest {

    DurationXmlAdapter adapter = new DurationXmlAdapter();

    @Test
    public void testPositive() {
        String stringValue = "PT30S";
        Duration pt30S = adapter.unmarshal(stringValue);
        String xmlDuration = adapter.marshal(pt30S);

        assertEquals(stringValue, xmlDuration);
    }

    @Test
    public void testNegative() {
        String stringValue = "PT-30S";
        Duration pt30S = adapter.unmarshal(stringValue);

        assertEquals(stringValue, pt30S.toString());

        String xmlDuration = adapter.marshal(pt30S);

        assertNotSame(stringValue, pt30S.toString());

        assertEquals("-PT30S", xmlDuration);

    }
}
