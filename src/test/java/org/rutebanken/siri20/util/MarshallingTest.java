package org.rutebanken.siri20.util;

import org.junit.BeforeClass;
import org.junit.Test;
import uk.org.siri.siri20.Siri;

import java.io.IOException;
import java.io.RandomAccessFile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.rutebanken.siri20.util.SiriJson.parseJson;
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

        //Parse Json
        Siri parsedSiri = parseJson(json);

        assertNotNull(parsedSiri);

        // Convert to XML
        String marshalledXml = toXml(parsedSiri);

        // Compare
        assertEquals(xml, marshalledXml);
    }


    private static String readFile(String path) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(path, "rw");
        byte[] contents = new byte[(int)raf.length()];
        raf.readFully(contents);
        return new String(contents);
    }
}
