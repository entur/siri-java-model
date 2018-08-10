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

package org.rutebanken.siri20.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import uk.org.siri.siri20.Siri;

import java.io.IOException;
import java.io.OutputStream;

public class SiriJson {

    private static  ObjectMapper mapper;


    static {
        init();
    }

    private static void init(){
        if (mapper == null) {
            mapper = new ObjectMapper();

            // Configuration Deserialization
            mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
            mapper.configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
            mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
            mapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true);
            mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

            // Configuration Serialization
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
            mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            mapper.configure(SerializationFeature.CLOSE_CLOSEABLE, true);
            mapper.configure(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS, false);
            mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
            mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
            mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
            mapper.configure(SerializationFeature.FLUSH_AFTER_WRITE_VALUE, true);

            // JAXB annotation
            mapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector(TypeFactory.defaultInstance()));

            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            mapper.registerModule(new AfterburnerModule());
            mapper.setDateFormat(new ISO8601DateFormat());
        }
    }

    public static String toJson(Siri siri) throws IOException {
        return mapper.writeValueAsString(siri);
    }

    public static void toJson(Siri siri, OutputStream out) throws IOException {
        mapper.writeValue(out, siri);
    }

}