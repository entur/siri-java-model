# siri-java-model [![CircleCI](https://circleci.com/gh/entur/siri-java-model/tree/master.svg?style=svg)](https://circleci.com/gh/entur/siri-java-model/tree/master)
```mvn clean install```

Generates Java API based on the xsds for SIRI Versions 
- 1.0
- 1.3
- 1.4
- 2.0

Source 1.0 - 1.4 http://user47094.vs.easily.co.uk/siri/ 

Source 2.0 -> https://www.vdv.de/siri.aspx 

Also provides XML-validation through method `SiriValidator.validate`.

## Java modules (JPMS)

The jar is a named module, `org.entur.siri`. It is an `open module`, because the JAXB runtime that you supply needs reflective access to the generated classes. It requires `jakarta.xml.bind` and Jackson. You must also provide a JAXB implementation, for example `com.sun.xml.bind:jaxb-impl`.

```java
module my.app {
    requires org.entur.siri;
}
```

## Upgrading to 3.0

**Breaking change: the generated packages are renamed.**
The XML namespaces and the wire format are unchanged. Update your imports:

| Before | After |
|---|---|
| `uk.org.siri.siri20` / `siri21` | `org.entur.siri.schema.v20.siri` / `v21.siri` |
| `uk.org.ifopt.siri20` / `siri21` | `org.entur.siri.schema.v20.ifopt` / `v21.ifopt` |
| `uk.org.acbs.siri20` / `siri21` | `org.entur.siri.schema.v20.acsb` / `v21.acsb` |
| `eu.datex2.siri20.schema._2_0rc1._2_0` / `siri21...` | `org.entur.siri.schema.v20.datex2` / `v21.datex2` |
| `net.opengis.gml.siri` | `org.entur.siri.schema.v21.gml` |


**Breaking change: `NamespacePrefixMapper` is removed from `SiriXml`.**
The overloads `toXml(Siri, NamespacePrefixMapper)` and `toXml(Siri, NamespacePrefixMapper, OutputStream)` in `org.entur.siri21.util.SiriXml` and `org.entur.siri20.util.SiriXml` now take a `Consumer<Marshaller>` instead. The `JAXBNamespacePrefixMapper` dependency is removed, because it shares a package with the JAXB reference implementation and cannot be used on the module path.

Before:

```java
SiriXml.toXml(siri, myNamespacePrefixMapper);
```

After: set the property of your own JAXB implementation on the marshaller.

```java
SiriXml.toXml(siri, marshaller -> {
    try {
        marshaller.setProperty("org.glassfish.jaxb.namespacePrefixMapper", myNamespacePrefixMapper);
    } catch (PropertyException e) {
        throw new IllegalStateException(e);
    }
});
```

The property name depends on your JAXB implementation. Use the one that your implementation documents. If your own code imports `com.sun.xml.bind.marshaller.NamespacePrefixMapper`, add the dependency that provides it yourself. This library no longer brings it in.

**Breaking change: the SIRI 2.0 utility package is renamed.**
`org.rutebanken.siri20.util` (`SiriXml`, `SiriJson`) is now `org.entur.siri20.util`, which matches `org.entur.siri21.util`. Update your imports. There are no deprecated forwarders in the old package.

The module name is now explicit (`org.entur.siri`). It was derived from the jar file name (`siri.java.model`) before.
