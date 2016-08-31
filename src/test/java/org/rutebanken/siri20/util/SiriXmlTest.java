package org.rutebanken.siri20.util;

import org.junit.BeforeClass;
import org.junit.Test;
import org.rutebanken.validator.SiriValidator;
import uk.org.siri.siri20.Siri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import static org.junit.Assert.*;
import static org.rutebanken.siri20.util.SiriXml.parseXml;
import static org.rutebanken.siri20.util.SiriXml.toXml;
import static org.rutebanken.validator.SiriValidator.validate;

public class SiriXmlTest {

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

        File tmpFile = File.createTempFile("tmp-siri-xml", ".xml");
        FileOutputStream fos = new FileOutputStream(tmpFile);

        boolean isValid = validate(xml, SiriValidator.VERSION.VERSION_2_0, System.out);
        assertTrue("Example-file is not valid", isValid);

        Siri s = parseXml(xml);
        String generatedXml = toXml(s);

        isValid = validate(generatedXml, SiriValidator.VERSION.VERSION_2_0, System.out);
        assertTrue("Generated XML is not valid", isValid);
    }

    private static String readFile(String path) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(path, "rw");
        byte[] contents = new byte[(int)raf.length()];
        raf.readFully(contents);
        return new String(contents);
    }
}