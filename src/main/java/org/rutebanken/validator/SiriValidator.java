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

package org.rutebanken.validator;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.URL;

public class SiriValidator {

    private static JAXBContext siri20jaxbContext;
    private static JAXBContext siri10jaxbContext;
    private static JAXBContext siri13jaxbContext;
    private static JAXBContext siri14jaxbContext;

    static {
        try {
            init();
        } catch (JAXBException e) {
            throw new InstantiationError();
        }
    }

    private static void init() throws JAXBException {
        if (siri10jaxbContext == null) {
            siri10jaxbContext = JAXBContext.newInstance(uk.org.siri.siri10.Siri.class);
        }
        if (siri13jaxbContext == null) {
            siri13jaxbContext = JAXBContext.newInstance(uk.org.siri.siri13.Siri.class);
        }
        if (siri14jaxbContext == null) {
            siri14jaxbContext = JAXBContext.newInstance(uk.org.siri.siri14.Siri.class);
        }
        if (siri20jaxbContext == null) {
            siri20jaxbContext = JAXBContext.newInstance(uk.org.siri.siri20.Siri.class);
        }
    }
    /**
     * Validates xml against xsd
     *
     * Result is printed to System.out
     *
     * @param xml
     * @param version
     * @return
     * @throws JAXBException
     * @throws SAXException
     */
    public static boolean validate(String xml, Version version) throws JAXBException, SAXException {
        return validate(xml, version, System.out);
    }

    /**
     * Validates xml against xsd
     *
     * Result is printed to provided PrintStream
     *
     * @param xml
     * @param version
     * @param out
     * @return
     * @throws JAXBException
     * @throws SAXException
     */
    public static boolean validate(String xml, Version version, PrintStream out) throws JAXBException, SAXException {
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        Schema schema = sf.newSchema(getXsdRelativePath(version));

        Unmarshaller unmarshaller = getVersionSpecificUnmarshaller(version);
        unmarshaller.setSchema(schema);
        SiriValidationEventHandler handler = new SiriValidationEventHandler();
        unmarshaller.setEventHandler(handler);
        unmarshaller.unmarshal(new StringReader(xml));

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

    private static Unmarshaller getVersionSpecificUnmarshaller(Version version) throws JAXBException {
        switch (version) {
            case VERSION_1_0:
                return siri10jaxbContext.createUnmarshaller();
            case VERSION_1_3:
                return siri13jaxbContext.createUnmarshaller();
            case VERSION_1_4:
                return siri14jaxbContext.createUnmarshaller();
            case VERSION_2_0:
            default:
                return siri20jaxbContext.createUnmarshaller();
        }
    }

    private static URL getXsdRelativePath(Version version) {

        String path;
        switch (version) {
            case VERSION_1_0:
                path = "siri-1.0/xsd/siri.xsd";
                break;
            case VERSION_1_3:
                path = "siri-1.3/xsd/siri.xsd";
                break;
            case VERSION_1_4:
                path = "siri-1.4/xsd/siri.xsd";
                break;
            case VERSION_2_0:
            default:
                path = "siri-2.0/xsd/siri.xsd";
        }

        return new SiriValidator().getClass().getClassLoader().getResource(path);
    }

    public static enum Version {VERSION_1_0, VERSION_1_3, VERSION_1_4, VERSION_2_0}
}
