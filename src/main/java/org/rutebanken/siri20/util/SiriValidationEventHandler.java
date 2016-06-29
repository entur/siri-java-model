package org.rutebanken.siri20.util;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import java.util.ArrayList;
import java.util.List;

class SiriValidationEventHandler implements ValidationEventHandler {

    List<ValidationEvent> events = new ArrayList<>();

    public boolean handleEvent(ValidationEvent event) {
        events.add(event);
        return true;
    }

}
