package com.koliving.api.email;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Component
@AllArgsConstructor
public class EmailTemplateUtil {

    private final TemplateEngine templateEngine;

    public String generateEmail(String templateName, Map<String, Object> args) {
        Context context = new Context();
        context.setVariables(args);

        return templateEngine.process(templateName, context);
    }
}
