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

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Resolves schema locations relative to the schema document that references them, without
 * consulting any XML catalog.
 * <p>
 * Since JDK 24 (JDK-8344800) the JDK built-in XML catalog rewrites every schema location ending
 * in {@code XMLSchema.xsd} to its own copy of the XML Schema schema-for-schemas. The SIRI 2.1
 * schemas (which are part of the standard and are shipped unmodified) store the W3C xml
 * namespace schema as {@code xml/2009/01/XMLSchema.xsd}, so without this resolver loading the
 * SIRI 2.1 schema on JDK 24+ fails with {@code Cannot resolve the name 'xml:lang'}.
 * <p>
 * The returned {@link LSInput} carries only the resolved system id: no stream is opened here.
 * Xerces opens the stream itself, and only for documents it has not already traversed, so
 * nothing is leaked for the many schemas that are included or imported more than once.
 * <p>
 * Xerces does not apply its {@code accessExternalSchema} check to locations that a resource
 * resolver has resolved: the resolver is the access policy. This resolver therefore only
 * resolves locations that stay inside the bundled schema tree, that is {@code file:} and
 * {@code jar:} URLs. Anything else is left to Xerces, which then applies its own access check
 * and catalog.
 */
final class RelativeSchemaResourceResolver implements LSResourceResolver {

    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        if (systemId == null || baseURI == null) {
            return null;
        }
        URL resolved;
        try {
            // Deliberately the deprecated URL(URL, String) constructor: the base is a jar: URL
            // when the schemas are loaded from the published jar, and jar: URIs are opaque, so
            // URI.resolve() would return the relative reference unchanged. The URL constructor
            // resolves it correctly for both file: and jar: bases.
            resolved = new URL(new URL(baseURI), systemId);
        } catch (MalformedURLException e) {
            return null;
        }
        if (!isLocal(resolved)) {
            return null;
        }
        return new SystemIdInput(publicId, resolved.toExternalForm(), baseURI);
    }

    private static boolean isLocal(URL url) {
        String protocol = url.getProtocol();
        return "file".equals(protocol) || "jar".equals(protocol);
    }

    /**
     * An {@link LSInput} that only identifies the resource; the parser opens it.
     */
    private static final class SystemIdInput implements LSInput {

        private final String publicId;
        private final String systemId;
        private final String baseURI;

        SystemIdInput(String publicId, String systemId, String baseURI) {
            this.publicId = publicId;
            this.systemId = systemId;
            this.baseURI = baseURI;
        }

        @Override
        public String getPublicId() {
            return publicId;
        }

        @Override
        public String getSystemId() {
            return systemId;
        }

        @Override
        public String getBaseURI() {
            return baseURI;
        }

        @Override
        public Reader getCharacterStream() {
            return null;
        }

        @Override
        public InputStream getByteStream() {
            return null;
        }

        @Override
        public String getStringData() {
            return null;
        }

        @Override
        public String getEncoding() {
            return null;
        }

        @Override
        public boolean getCertifiedText() {
            return false;
        }

        @Override
        public void setCharacterStream(Reader characterStream) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setByteStream(InputStream byteStream) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setStringData(String stringData) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setSystemId(String systemId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setPublicId(String publicId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setBaseURI(String baseURI) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setEncoding(String encoding) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCertifiedText(boolean certifiedText) {
            throw new UnsupportedOperationException();
        }
    }
}
