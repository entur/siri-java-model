package org.entur.siri;

import org.entur.siri.validator.SiriValidator;

public class UnsupportedSiriVersionException extends RuntimeException {

    final SiriValidator.Version version;

    public UnsupportedSiriVersionException(SiriValidator.Version version) {
        this.version = version;
    }

    @Override
    public String getMessage() {
        return "Version " + version + " is unsupported.";
    }
}
