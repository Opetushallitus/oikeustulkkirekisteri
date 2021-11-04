package fi.vm.sade.oikeustulkkirekisteri.service;

import java.io.IOException;
import java.util.Map;

public interface EmailTemplateRenderer {

    String renderTemplate(String templateName, Map<String, String> params) throws IOException;
}
