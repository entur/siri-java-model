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

package org.entur.siri20.util;

import jakarta.xml.bind.Marshaller;
import org.junit.BeforeClass;
import org.junit.Test;
import org.entur.siri.schema.v20.siri.Siri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

import static org.entur.siri20.util.SiriXml.parseXml;
import static org.entur.siri20.util.SiriXml.toXml;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SiriXmlMarshallerCustomizerTest {

    private static Siri siri;

    @BeforeClass
    public static void init() throws Exception {
        String xml = new String(Files.readAllBytes(Paths.get("src/test/resources/et.xml")), StandardCharsets.UTF_8);
        siri = parseXml(xml);
    }

    @Test
    public void testCustomizerIsAppliedToMarshaller() throws Exception {
        AtomicInteger calls = new AtomicInteger();

        String formatted = toXml(siri, marshaller -> {
            calls.incrementAndGet();
            setFormattedOutput(marshaller);
        });

        assertEquals(1, calls.get());
        assertTrue("Expected indented output", isIndented(formatted));
    }

    @Test
    public void testNullCustomizerGivesSameOutputAsDefault() throws Exception {
        assertEquals(toXml(siri), toXml(siri, null));
    }

    @Test
    public void testCustomizerIsAppliedWhenWritingToOutputStream() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        SiriXml.toXml(siri, SiriXmlMarshallerCustomizerTest::setFormattedOutput, out);

        String formatted = new String(out.toByteArray(), StandardCharsets.UTF_8);
        assertTrue("Expected indented output", isIndented(formatted));
    }

    @Test
    public void testCustomizerDoesNotLeakIntoLaterCalls() throws Exception {
        toXml(siri, SiriXmlMarshallerCustomizerTest::setFormattedOutput);

        assertFalse("Customizer must only affect its own marshaller", isIndented(toXml(siri)));
    }

    private static boolean isIndented(String xml) {
        return xml.split("\n").length > 5;
    }

    private static void setFormattedOutput(Marshaller marshaller) {
        try {
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
