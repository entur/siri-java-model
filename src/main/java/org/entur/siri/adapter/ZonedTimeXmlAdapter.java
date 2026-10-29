package org.entur.siri.adapter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * JAXB adapter for {@code xsd:time} bound to {@link ZonedDateTime}. Written by hand so that XJC does not
 * generate an adapter class into the {@code org.w3._2001.xmlschema} package, which would be a split package.
 */
public class ZonedTimeXmlAdapter extends XmlAdapter<String, ZonedDateTime> {

    @Override
    public ZonedDateTime unmarshal(String value) {
        return ZonedDateTimeAdapter.parse(value);
    }

    @Override
    public String marshal(ZonedDateTime value) {
        return DateTimeFormatter.ISO_OFFSET_TIME.format(value);
    }
}
