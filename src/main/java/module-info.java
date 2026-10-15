open module org.entur.siri {
    requires transitive jakarta.xml.bind;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.module.jakarta.xmlbind;

    exports org.entur.siri;
    exports org.entur.siri.adapter;
    exports org.entur.siri.validator;
    exports org.entur.siri21.util;
    exports org.entur.siri20.util;
    exports org.entur.siri.schema.v20.acsb;
    exports org.entur.siri.schema.v20.datex2;
    exports org.entur.siri.schema.v20.ifopt;
    exports org.entur.siri.schema.v20.siri;
    exports org.entur.siri.schema.v21.acsb;
    exports org.entur.siri.schema.v21.datex2;
    exports org.entur.siri.schema.v21.gml;
    exports org.entur.siri.schema.v21.ifopt;
    exports org.entur.siri.schema.v21.siri;
}
