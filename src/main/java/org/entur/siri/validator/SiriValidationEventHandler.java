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

import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.ValidationEventHandler;
import java.util.ArrayList;
import java.util.List;

public class SiriValidationEventHandler implements ValidationEventHandler {

    public List<ValidationEvent> events = new ArrayList<>();

    public boolean handleEvent(ValidationEvent event) {
        events.add(event);
        return true;
    }

    public boolean isValid() {
        return events.size() == 0;
    }

}
