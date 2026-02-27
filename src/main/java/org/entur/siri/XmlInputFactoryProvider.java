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

package org.entur.siri;

import javax.xml.stream.XMLInputFactory;

/**
 * Provides a shared {@link XMLInputFactory} when the StAX provider is Woodstox
 * (thread-safe after initialization), otherwise creates a new factory per call.
 */
public class XmlInputFactoryProvider {

    private static final XMLInputFactory SHARED_XML_INPUT_FACTORY = createSharedFactory();

    /**
     * Returns a shared instance if the StAX provider is Woodstox (thread-safe after
     * initialization), otherwise creates a new factory per call (spec-safe default).
     */
    public static XMLInputFactory getXmlInputFactory() {
        if (SHARED_XML_INPUT_FACTORY != null) {
            return SHARED_XML_INPUT_FACTORY;
        }
        return XMLInputFactory.newInstance();
    }

    private static XMLInputFactory createSharedFactory() {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        // Woodstox factories are thread-safe after initialization.
        // https://github.com/FasterXML/Woodstox4/blob/master/release-notes/USAGE
        if (factory.getClass().getName().startsWith("com.ctc.wstx.")) {
            return factory;
        }
        return null;
    }
}
