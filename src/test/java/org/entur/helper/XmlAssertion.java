package org.entur.helper;

import static org.junit.Assert.assertEquals;

public class XmlAssertion {

    /**
     * Compares SIRI-xml without the namespace-definitions - they may be in random order
     * @param xml1
     * @param xml2
     */
    public static void assertXml(String xml1, String xml2) {
        int startIndex1 = xml1.indexOf("<ServiceDelivery");
        int startIndex2 = xml2.indexOf("<ServiceDelivery");

        assertEquals(xml1.substring(startIndex1), xml2.substring(startIndex2));
    }
}
