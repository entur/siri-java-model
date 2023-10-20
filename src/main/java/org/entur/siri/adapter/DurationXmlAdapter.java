package org.entur.siri.adapter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.Duration;

public class DurationXmlAdapter extends XmlAdapter<String, Duration> {
    @Override
    public Duration unmarshal(String stringValue) {
        return stringValue != null ? Duration.parse(stringValue) : null;
    }

    /**
     * XML datatype xs:duration is only partially based on ISO-8601, and differs when it comes to negative durations.
     *
     * While ISO-8601 allows for negative values for each part of the duration (e.g. <code>PT-1M-30S</code>), an xs:duration only
     * allows the entire duration to be negative - i.e. <code>-PT1M30S</code>
     *
     * This method rewrites the produced Duration-string to produce a valid xs:duration
     *
     * @param duration
     * @return
     */
    @Override
    public String marshal(Duration duration) {
        if (duration != null) {

            if (duration.isNegative()) {
                StringBuilder xmlDurationStr = new StringBuilder();
                xmlDurationStr.append("-");
                xmlDurationStr.append(
                        duration.toString().replaceAll("-", "")
                );
                return xmlDurationStr.toString();
            } else {
                return duration.toString();
            }
        }
        return null;
    }
}