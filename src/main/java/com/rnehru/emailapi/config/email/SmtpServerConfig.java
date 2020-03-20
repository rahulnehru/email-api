package com.rnehru.emailapi.config.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmtpServerConfig {

    @Value(value = "${mail.smtp.port}")
    public String smtpPort;

    @Value(value = "${mail.smtp.host}")
    public String smtpHost;

    @Value(value = "${mail.from}")
    public String fromAddress;

    @Value(value = "${mail.smtp.tls.password}")
    public String smtpPassword;

    @Value(value = "${mail.smtp.tls.userid}")
    public String smtpUserId;

}
