package fi.vm.sade.oikeustulkkirekisteri.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

public class JacksonFeature extends JacksonJaxbJsonProvider {
    public JacksonFeature() {
        super();
        setMapper(new ObjectMapper() {
            private static final long serialVersionUID = 1L;
            {
                registerModule(new Jdk8Module());
                registerModule(new JavaTimeModule());
                // True
                enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

                // False
                disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            }
        });
    }
}
