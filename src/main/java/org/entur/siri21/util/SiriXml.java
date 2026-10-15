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

import org.entur.siri.XmlInputFactoryProvider;
import uk.org.siri.siri21.Siri;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.function.Consumer;

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

        XMLInputFactory xmlif = XmlInputFactoryProvider.getXmlInputFactory();
        XMLStreamReader xmler = xmlif.createXMLStreamReader(new StringReader(xml));

        return (Siri) jaxbUnmarshaller.unmarshal(xmler);
    }

    public static Siri parseXml(InputStream inputStream) throws JAXBException, XMLStreamException {
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        XMLInputFactory xmlif = XmlInputFactoryProvider.getXmlInputFactory();
        XMLStreamReader xmler = xmlif.createXMLStreamReader(inputStream);

        return (Siri) jaxbUnmarshaller.unmarshal(xmler);
    }

    public static String toXml(Siri siri) throws JAXBException {
        return toXml(siri, null);
    }

    public static String toXml(Siri siri, Consumer<Marshaller> marshallerCustomizer) throws JAXBException {

        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        if (marshallerCustomizer != null) {
            marshallerCustomizer.accept(jaxbMarshaller);
        }
        StringWriter sw = new StringWriter();
        jaxbMarshaller.marshal(siri, sw);

        return sw.toString();
    }

    public static void toXml(Siri siri, Consumer<Marshaller> marshallerCustomizer, OutputStream out) throws JAXBException, IOException {
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        if (marshallerCustomizer != null) {
            marshallerCustomizer.accept(jaxbMarshaller);
        }
        jaxbMarshaller.marshal(siri, out);
    }

}

