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

package org.entur.siri;

import org.junit.Test;

/**
 * Verifies that XXE (XML External Entity) attacks are blocked by the XML parser configuration.
 */
public class XmlInputFactoryProviderTest {

    private static final String XXE_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<!DOCTYPE foo [<!ENTITY xxe SYSTEM \"file:///etc/passwd\">]>" +
        "<Siri version=\"2.0\" xmlns=\"http://www.siri.org.uk/siri\">" +
        "  <ServiceDelivery>" +
        "    <ResponseTimestamp>&xxe;</ResponseTimestamp>" +
        "  </ServiceDelivery>" +
        "</Siri>";

    private static final String XXE_XML_21 =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<!DOCTYPE foo [<!ENTITY xxe SYSTEM \"file:///etc/passwd\">]>" +
        "<Siri version=\"2.1\" xmlns=\"http://www.siri.org.uk/siri\">" +
        "  <ServiceDelivery>" +
        "    <ResponseTimestamp>&xxe;</ResponseTimestamp>" +
        "  </ServiceDelivery>" +
        "</Siri>";

    @Test(expected = Exception.class)
    public void testXxeBlockedSiri20() throws Exception {
        org.rutebanken.siri20.util.SiriXml.parseXml(XXE_XML);
    }

    @Test(expected = Exception.class)
    public void testXxeBlockedSiri21() throws Exception {
        org.entur.siri21.util.SiriXml.parseXml(XXE_XML_21);
    }
}
