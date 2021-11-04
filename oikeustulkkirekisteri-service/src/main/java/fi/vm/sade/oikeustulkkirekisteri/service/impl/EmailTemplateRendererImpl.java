package fi.vm.sade.oikeustulkkirekisteri.service.impl;

import fi.vm.sade.oikeustulkkirekisteri.service.EmailTemplateRenderer;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

@Service
public class EmailTemplateRendererImpl implements EmailTemplateRenderer {

    @Override
    public String renderTemplate(String templateName, Map<String, String> params) throws IOException {
        String content = readFile("classpath:email.template/" + templateName);

        for (String key : params.keySet()) {
            content = content.replace("{{" + key + "}}", params.get(key));
        }
        return content;
    }

    private String readFile(String filePath) throws IOException {
        File file = ResourceUtils.getFile(filePath);
        return Files.readString(file.toPath());
    }

}
