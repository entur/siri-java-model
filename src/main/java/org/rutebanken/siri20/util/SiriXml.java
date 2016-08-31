package org.rutebanken.siri20.util;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import uk.org.siri.siri20.Siri;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
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
        out.flush();
        out.close();
    }

}

