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

import org.entur.siri.validator.SiriValidator;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.org.siri.siri21.Siri;

import java.io.IOException;
import java.io.RandomAccessFile;

import static org.entur.siri.validator.SiriValidator.validate;
import static org.entur.siri21.util.SiriXml.parseXml;
import static org.entur.siri21.util.SiriXml.toXml;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SiriXmlTest {

    private static String xml;

    @BeforeClass
    public static void init() throws IOException {

        xml = readFile("src/test/resources/et.xml");

        //Removing indentation and newlines to match unformatted xml
        xml = xml.replace("\n", "");
        while (xml.indexOf("  ") > 0) {
            xml = xml.replace("  ", "");
        }
    }

    @Test
    public void testParseXml() throws Exception {

    }

    @Test
    public void testToXml() throws Exception {

        long t1 = System.currentTimeMillis();
        Siri s = parseXml(xml);
        System.out.println("Parsing xml - coldstart - took: " + (System.currentTimeMillis() - t1));
        t1 = System.currentTimeMillis();
        s = parseXml(xml);
        System.out.println("Parsing xml - warmstart - took: " + (System.currentTimeMillis() - t1));

        long t2 = System.currentTimeMillis();
        String jaxbXml = toXml(s);
        System.out.println("to xml: " + (System.currentTimeMillis() - t2));

        assertEquals(xml, jaxbXml);
    }

    @Test
    public void testValidate() throws Exception {

        boolean isValid = validate(xml, SiriValidator.Version.VERSION_2_1, System.out);
        assertTrue("Example-file is not valid", isValid);

        Siri s = parseXml(xml);
        String generatedXml = toXml(s);

        isValid = validate(generatedXml, SiriValidator.Version.VERSION_2_1, System.out);
        assertTrue("Generated XML is not valid", isValid);
    }

    private static String readFile(String path) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(path, "rw");
        byte[] contents = new byte[(int)raf.length()];
        raf.readFully(contents);
        return new String(contents);
    }
}