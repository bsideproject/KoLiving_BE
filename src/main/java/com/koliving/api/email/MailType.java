package com.koliving.api.email;

public enum MailType {
    AUTH("auth-email-template"),
    CONTACT("contact-email-template");

    private final String template;

    MailType(String template) {
        this.template = template;
    }

    public String getTemplateName() {
        return this.template;
    }
}
