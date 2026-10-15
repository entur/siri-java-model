open module org.entur.siri {
    requires transitive jakarta.xml.bind;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.module.jakarta.xmlbind;

    exports org.entur.siri;
    exports org.entur.siri.adapter;
    exports org.entur.siri.validator;
    exports org.entur.siri21.util;
    exports org.rutebanken.siri20.util;
    exports eu.datex2.siri20.schema._2_0rc1._2_0;
    exports eu.datex2.siri21.schema._2_0rc1._2_0;
    exports net.opengis.gml.siri;
    exports org.w3._2001.xmlschema;
    exports uk.org.acbs.siri20;
    exports uk.org.acbs.siri21;
    exports uk.org.ifopt.siri20;
    exports uk.org.ifopt.siri21;
    exports uk.org.siri.siri20;
    exports uk.org.siri.siri21;
}
