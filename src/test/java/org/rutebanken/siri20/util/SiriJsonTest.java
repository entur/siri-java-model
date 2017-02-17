package org.rutebanken.siri20.util;

import org.junit.BeforeClass;
import org.junit.Test;
import uk.org.siri.siri20.Siri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import static org.junit.Assert.*;
import static org.rutebanken.siri20.util.SiriJson.parseJson;
import static org.rutebanken.siri20.util.SiriJson.toJson;
import static org.rutebanken.siri20.util.SiriXml.parseXml;

public class SiriJsonTest {

    private static String xml;

    @BeforeClass
    public static void init() throws IOException {

        xml = readFile("src/test/resources/vm-small.xml");

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

        Siri parsedSiri = parseJson(json);

        assertNotNull(parsedSiri);
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
