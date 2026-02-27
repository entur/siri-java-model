# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**siri-java-model** is a Java library (by Entur AS) that generates Java objects from SIRI (Service Interface for Real-time Information) XSD schemas. It supports SIRI 2.0 and 2.1. Published to Maven Central under `org.entur:siri-java-model`.

## Build Commands

```bash
# Build and run tests
mvn clean install

# Run tests only
mvn test

# Run a single test class
mvn test -Dtest=SiriXmlTest

# Run a single test method
mvn test -Dtest=SiriXmlTest#testParseXml

# Generate sources without running tests
mvn generate-sources
```

**Note:** Tests run with `-Duser.timezone=Europe/Oslo` (configured in surefire plugin).

## Architecture

### Code Generation (bulk of the codebase)

The `cxf-xjc-plugin` generates ~1,900 Java classes from XSD schemas during `generate-sources`:
- **SIRI 2.0** XSDs → `uk.org.siri.siri20.*` (plus `uk.org.ifopt.siri20`, `uk.org.acbs.siri20`, `eu.datex2.siri20`)
- **SIRI 2.1** XSDs → `uk.org.siri.siri21.*` (plus `uk.org.ifopt.siri21`, `uk.org.acbs.siri21`, `eu.datex2.siri21`)

Generated sources land in `target/generated-sources`. JAXB binding customizations are in `src/main/resources/siri-{2.0,2.1}/xjb/jaxb-bindings.xml` — these map `xsd:dateTime`/`xsd:time` to `java.time.ZonedDateTime` and `xsd:duration` to `java.time.Duration`.

### Handwritten Code (~9 files)

| Package | Class | Purpose |
|---------|-------|---------|
| `org.entur.siri.adapter` | `ZonedDateTimeAdapter` | JAXB adapter: XML dateTime ↔ `ZonedDateTime` |
| `org.entur.siri.adapter` | `DurationXmlAdapter` | JAXB adapter: XML duration ↔ `java.time.Duration` |
| `org.entur.siri.validator` | `SiriValidator` | Validates XML against SIRI 2.0/2.1 XSD schemas |
| `org.entur.siri.validator` | `SiriValidationEventHandler` | Collects validation errors |
| `org.entur.siri21.util` | `SiriXml` | SIRI 2.1 XML marshalling/unmarshalling (JAXB) |
| `org.entur.siri21.util` | `SiriJson` | SIRI 2.1 JSON conversion (Jackson + Jakarta XML Bind) |
| `org.rutebanken.siri20.util` | `SiriXml` | SIRI 2.0 XML marshalling/unmarshalling (legacy package) |
| `org.entur.siri` | `UnsupportedSiriVersionException` | Exception for unsupported SIRI versions |

### Key Design Decisions

- **Jakarta EE**: Uses `jakarta.xml.bind` (3.0+), not legacy `javax.xml.bind`.
- **Java 11** target (source and bytecode).
- **JUnit 4** for tests.
- **JSON support exists only for SIRI 2.1** (`SiriJson`), not 2.0.
- **SIRI 2.0 utility lives in legacy package** `org.rutebanken.siri20.util` (historical).

### Test Data

XML test fixtures in `src/test/resources/`: `et.xml`, `et-2_1.xml` (Estimated Timetable), `vm.xml`, `vm-2_1.xml`, `vm-small.xml` (Vehicle Monitoring), `sx-2_1.xml` (Situation Exchange).
