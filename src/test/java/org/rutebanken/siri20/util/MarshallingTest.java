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

package org.rutebanken.siri20.util;

import org.junit.BeforeClass;
import org.junit.Test;
import uk.org.siri.siri20.Siri;

import java.io.IOException;
import java.io.RandomAccessFile;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.entur.helper.XmlAssertion.assertXml;
import static org.junit.Assert.assertNotNull;
import static org.rutebanken.siri20.util.SiriJson.toJson;
import static org.rutebanken.siri20.util.SiriXml.parseXml;
import static org.rutebanken.siri20.util.SiriXml.toXml;

public class MarshallingTest {

    private static String xml;

    @BeforeClass
    public static void init() throws IOException {

        xml = readFile("src/test/resources/vm.xml");

        //Removing indentation and newlines to match unformatted xml
        xml = xml.replace("\n", "");
        while (xml.indexOf("  ") > 0) {
            xml = xml.replace("  ", "");
        }
    }


    @Test
    public void testToFromJsonAndXml() throws Exception {

        //Read SIRI-object from XML
        Siri s = parseXml(xml);

        // Convert to JSON
        String json = toJson(s);

        assertNotNull(json);
        assertFalse(json.isEmpty());
        assertTrue(json.length() > xml.length()/2);


        // Convert to XML
        String marshalledXml = toXml(s);

        // Compare
        assertXml(xml, marshalledXml);
    }


    private static String readFile(String path) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(path, "rw");
        byte[] contents = new byte[(int)raf.length()];
        raf.readFully(contents);
        return new String(contents);
    }
}
