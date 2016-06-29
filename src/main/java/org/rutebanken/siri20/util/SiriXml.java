package org.rutebanken.siri20.util;

import org.xml.sax.SAXException;
import uk.org.siri.siri20.Siri;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;

public class SiriXml {

    private static JAXBContext jaxbContext;

    static {
        try {
            init();
        } catch (JAXBException e) {
            throw new InstantiationError();
        }
    }

    private static void init() throws JAXBException {
        if (jaxbContext == null) {
            jaxbContext = JAXBContext.newInstance(Siri.class);
        }
    }

    public static Siri parseXml(String xml) throws JAXBException {
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        return (Siri) jaxbUnmarshaller.unmarshal(new StringReader(xml));
    }

    public static String toXml(Siri siri) throws JAXBException {
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        StringWriter sw = new StringWriter();
        jaxbMarshaller.marshal(siri, sw);

        return sw.toString();
    }

    public static void toXml(Siri siri, OutputStream out) throws JAXBException, IOException {
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.marshal(siri, out);
        out.flush();
        out.close();
    }

    /**
     * Validates xml against xsd
     *
     * Result is printed to System.out
     *
     * @param xml
     * @param xsdPathName
     * @return
     * @throws JAXBException
     * @throws SAXException
     */
    public static boolean validate(String xml, String xsdPathName) throws JAXBException, SAXException {
        return validate(xml, xsdPathName, System.out);
    }

    /**
     * Validates xml against xsd
     *
     * Result is printed to provided PrintStream
     *
     * @param xml
     * @param xsdPathName
     * @param out
     * @return
     * @throws JAXBException
     * @throws SAXException
     */
    public static boolean validate(String xml, String xsdPathName, PrintStream out) throws JAXBException, SAXException {
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        Schema schema = sf.newSchema(new File(xsdPathName));

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setSchema(schema);
        SiriValidationEventHandler handler = new SiriValidationEventHandler();
        unmarshaller.setEventHandler(handler);
        Siri validSiri = (Siri) unmarshaller.unmarshal(new StringReader(xml));

        out.println("Found " + handler.events.size() + " errors");

        handler.events.forEach(event -> {
            out.println();
            out.println("EVENT");
            out.println("SEVERITY:  " + event.getSeverity());
            out.println("MESSAGE:  " + event.getMessage());
            out.println("LINKED EXCEPTION:  " + event.getLinkedException());
            out.println("LOCATOR");
            out.println("    LINE NUMBER:  " + event.getLocator().getLineNumber());
            out.println("    COLUMN NUMBER:  " + event.getLocator().getColumnNumber());
            out.println("    OFFSET:  " + event.getLocator().getOffset());
            out.println("    OBJECT:  " + event.getLocator().getObject());
            out.println("    NODE:  " + event.getLocator().getNode());
            out.println("    URL:  " + event.getLocator().getURL());
        });

        return handler.events.size() == 0;

    }
}

