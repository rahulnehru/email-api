package com.rnehru.emailapi.config.email;

import com.rnehru.emailapi.client.email.SmtpClient;
import com.rnehru.emailapi.client.email.TlsSmtpClient;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import com.rnehru.emailapi.client.email.EmailClient;

@Configuration
public class EmailClientProvider implements ClientProvider {

    private static final Logger logger = LoggerFactory.getLogger(EmailClientProvider.class);

    @Value("${mail.useTls}")
    private boolean useTls;

    @Getter
    private final EmailClient client;

    @Autowired
    public EmailClientProvider(SmtpServerConfig serverConfig) {
        client = useTls ? new TlsSmtpClient(serverConfig) : new SmtpClient(serverConfig);
        logger.info("Initialised {} TLS connection to SMTP server", useTls ? "with" : "without");
    }

}
