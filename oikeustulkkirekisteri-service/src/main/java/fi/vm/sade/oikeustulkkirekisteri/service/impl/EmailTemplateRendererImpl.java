package fi.vm.sade.oikeustulkkirekisteri.service.impl;

import fi.vm.sade.oikeustulkkirekisteri.service.EmailTemplateRenderer;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.Map;

@Service
public class EmailTemplateRendererImpl implements EmailTemplateRenderer {

    @Override
    public String renderTemplate(String templateName, Map<String, String> params) throws IOException  {
        String content = readFile("classpath:email.template/" + templateName);

        for (String key : params.keySet()) {
            content = content.replace("{{" + key + "}}", params.get(key));
        }
        return content;
    }

    private String readFile(String filePath) throws IOException {
        File file = ResourceUtils.getFile(filePath);
        InputStream inputStream = new FileInputStream(file);

        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        String content = resultStringBuilder.toString();
        inputStream.close();

        return content;
    }

}
