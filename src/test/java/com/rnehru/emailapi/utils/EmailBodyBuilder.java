package com.rnehru.emailapi.utils;

import com.rnehru.emailapi.model.EmailBody;

import java.util.List;

public class EmailBodyBuilder {

    private EmailBody emailBody;

    public EmailBodyBuilder() {
        this.emailBody = new EmailBody();
    }

    public EmailBodyBuilder withCcRecipients(List<String> recipients) {
        emailBody.setCcRecipients(recipients);
        return this;
    }

    public EmailBodyBuilder withToRecipients(List<String> recipients) {
        emailBody.setToRecipients(recipients);
        return this;
    }

    public EmailBodyBuilder withBccRecipients(List<String> recipients) {
        emailBody.setBccRecipients(recipients);
        return this;
    }

    public EmailBodyBuilder withBody(String body) {
        emailBody.setEmailBody(body);
        return this;
    }

    public EmailBodyBuilder withSubject(String subject) {
        emailBody.setSubject(subject);
        return this;
    }

    public EmailBody build() {
        return emailBody;
    }

}
