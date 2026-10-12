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

package org.entur.siri.validator;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.entur.siri.UnsupportedSiriVersionException;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SiriValidator {

    private static JAXBContext siri20jaxbContext;
    private static JAXBContext siri21jaxbContext;

    /**
     * One compiled {@link Schema} per version, built on first use. {@link Schema} is immutable
     * and thread-safe, and compiling the SIRI schema set is expensive.
     */
    private static final ConcurrentMap<Version, Schema> schemas = new ConcurrentHashMap<>();

    static {
        try {
            init();
        } catch (JAXBException e) {
            throw new InstantiationError();
        }
    }

    private static void init() throws JAXBException {
        if (siri20jaxbContext == null) {
            siri20jaxbContext = JAXBContext.newInstance(uk.org.siri.siri20.Siri.class);
        }
        if (siri21jaxbContext == null) {
            siri21jaxbContext = JAXBContext.newInstance(uk.org.siri.siri21.Siri.class);
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
     * Result is returned in EventHandler
     *
     * @param xml The XML string to be validated.
     * @param version The Siri validator version to be used when validating the xml.
     * @return SiriValidationEventHandler with the validation result.
     * @throws JAXBException
     * @throws SAXException
     */
    public static SiriValidationEventHandler validateAndGetHandler(String xml, Version version) throws JAXBException, SAXException {
        Unmarshaller unmarshaller = getVersionSpecificUnmarshaller(version);
        unmarshaller.setSchema(getSchema(version));
        SiriValidationEventHandler handler = new SiriValidationEventHandler();
        unmarshaller.setEventHandler(handler);
        unmarshaller.unmarshal(new StringReader(xml));
        return handler;
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
        SiriValidationEventHandler handler = validateAndGetHandler(xml, version);

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

    private static Schema getSchema(Version version) throws SAXException {
        try {
            return schemas.computeIfAbsent(version, v -> {
                try {
                    SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                    // Resolve imports between the bundled schemas ourselves: the JDK 24+ built-in XML catalog
                    // would otherwise shadow siri-2.1/xsd/xml/2009/01/XMLSchema.xsd and break SIRI 2.1 validation.
                    sf.setResourceResolver(new RelativeSchemaResourceResolver());
                    return sf.newSchema(getXsdRelativePath(v));
                } catch (SAXException e) {
                    throw new UncheckedSchemaException(e);
                }
            });
        } catch (UncheckedSchemaException e) {
            throw e.getCause();
        }
    }

    /**
     * Carries a {@link SAXException} out of {@link ConcurrentMap#computeIfAbsent}, which does not
     * allow checked exceptions.
     */
    private static final class UncheckedSchemaException extends RuntimeException {

        UncheckedSchemaException(SAXException cause) {
            super(cause);
        }

        @Override
        public synchronized SAXException getCause() {
            return (SAXException) super.getCause();
        }
    }

    private static Unmarshaller getVersionSpecificUnmarshaller(Version version) throws JAXBException {
        switch (version) {
            case VERSION_2_0:
                return siri20jaxbContext.createUnmarshaller();
            case VERSION_2_1:
                return siri21jaxbContext.createUnmarshaller();
            default:
                throw new UnsupportedSiriVersionException(version);
        }
    }

    private static URL getXsdRelativePath(Version version) {

        String path;
        switch (version) {
            case VERSION_2_0:
                path = "siri-2.0/xsd/siri.xsd";
                break;
            case VERSION_2_1:
                path = "siri-2.1/xsd/siri.xsd";
                break;
            default:
                throw new UnsupportedSiriVersionException(version);
        }

        return SiriValidator.class.getClassLoader().getResource(path);
    }

    public enum Version {VERSION_2_0, VERSION_2_1}
}
