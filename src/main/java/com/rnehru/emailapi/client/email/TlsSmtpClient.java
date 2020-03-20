package com.rnehru.emailapi.client.email;

import com.rnehru.emailapi.model.EmailBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rnehru.emailapi.config.email.SmtpServerConfig;
import com.rnehru.emailapi.exception.EmailNotSentException;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.validation.constraints.NotNull;
import java.util.Properties;

public final class TlsSmtpClient extends SmtpClientUtils implements EmailClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(TlsSmtpClient.class);
    private static final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";
    private static final String MAIL_STARTTLS_ENABLED = "mail.smtp.starttls.enable";
    private static final String MAIL_SMTP_AUTH ="mail.smtp.auth";

    @NotNull
    private final SmtpServerConfig config;

    public TlsSmtpClient(@NotNull SmtpServerConfig config) {
        this.config = config;
    }

    @Override
    public final Properties getProperties() {
        Properties prop = new Properties();
        prop.put(ENCODE_FILE_NAME, "true");
        prop.put(MAIL_SMTP_HOST, config.smtpHost);
        prop.put(MAIL_SMTP_PORT, config.smtpPort);
        prop.put(MAIL_TRANSPORT_PROTOCOL, "smtp");
        prop.put(MAIL_STARTTLS_ENABLED, "true");
        prop.put(MAIL_SMTP_AUTH, "true");
        return prop;
    }

    @Override
    public final void sendEmail(EmailBody emailBody) throws EmailNotSentException {
        try {
            Session session = Session.getDefaultInstance(getProperties());
            MimeMessage message = new MimeMessage(session);
            enrichMessage(message, config.fromAddress, emailBody);
            Transport transport = session.getTransport();
            transport.connect(config.smtpHost, config.smtpUserId, config.smtpPassword);
            transport.sendMessage(message, message.getAllRecipients());
            LOGGER.info("Sent email to [{}] with subject [{}]", flattenAddresses(message.getAllRecipients()), message.getSubject());
        } catch (EmailNotSentException | MessagingException e) {
            LOGGER.error("Could not send email due to {}", e.getMessage());
            throw new EmailNotSentException(e);
        }
    }

    @Override
    public final Logger getLogger() {
        return LOGGER;
    }


}
