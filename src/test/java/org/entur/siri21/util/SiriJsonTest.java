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

package org.entur.siri21.util;

import org.junit.BeforeClass;
import org.junit.Test;
import uk.org.siri.siri21.Siri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import static org.entur.siri21.util.SiriJson.toJson;
import static org.entur.siri21.util.SiriXml.parseXml;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SiriJsonTest {

    private static String xml;

    @BeforeClass
    public static void init() throws IOException {

        xml = readFile("src/test/resources/vm-2_1.xml");

        //Removing indentation and newlines to match unformatted xml
        xml = xml.replace("\n", "");
        while (xml.indexOf("  ") > 0) {
            xml = xml.replace("  ", "");
        }
    }


    @Test
    public void testToJson() throws Exception {

        //Read SIRI-object from XML
        Siri s = parseXml(xml);

        // Convert to JSON
        String json = toJson(s);

        assertNotNull(json);
        assertFalse(json.isEmpty());
        assertTrue(json.length() > xml.length()/2);

    }


    @Test
    public void testToJsonStream() throws Exception {

        //Read SIRI-object from XML
        Siri s = parseXml(xml);

        // Convert to JSON using OutputStream
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        toJson(s, out);

        out.flush();
        out.close();

        assertTrue(out.size() > 0);
    }

    private static String readFile(String path) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(path, "rw");
        byte[] contents = new byte[(int)raf.length()];
        raf.readFully(contents);
        return new String(contents);
    }
}
