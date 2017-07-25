package org.rutebanken.siri20.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import uk.org.siri.siri20.Siri;

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

    public static Siri parseXml(String xml) throws JAXBException, XMLStreamException {
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        
        XMLInputFactory xmlif = XMLInputFactory.newInstance();
        XMLStreamReader xmler = xmlif.createXMLStreamReader(new StringReader(xml));

        return (Siri) jaxbUnmarshaller.unmarshal(xmler);
    }

    public static Siri parseXml(InputStream inputStream) throws JAXBException, XMLStreamException {
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        XMLInputFactory xmlif = XMLInputFactory.newInstance();
        XMLStreamReader xmler = xmlif.createXMLStreamReader(inputStream);

        return (Siri) jaxbUnmarshaller.unmarshal(xmler);
    }

    public static String toXml(Siri siri) throws JAXBException {
        return toXml(siri, null);
    }

    public static String toXml(Siri siri, NamespacePrefixMapper customNamespacePrefixMapper) throws JAXBException {

        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        if (customNamespacePrefixMapper != null) {
            jaxbMarshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", customNamespacePrefixMapper);
        }
        StringWriter sw = new StringWriter();
        jaxbMarshaller.marshal(siri, sw);

        return sw.toString();
    }

    public static void toXml(Siri siri, NamespacePrefixMapper customNamespacePrefixMapper, OutputStream out) throws JAXBException, IOException {
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        if (customNamespacePrefixMapper != null) {
            jaxbMarshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", customNamespacePrefixMapper);
        }
        jaxbMarshaller.marshal(siri, out);
    }

}

