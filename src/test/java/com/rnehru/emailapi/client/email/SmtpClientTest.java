package com.rnehru.emailapi.client.email;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import com.rnehru.emailapi.model.EmailBody;
import com.rnehru.emailapi.utils.EmailBodyBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.rnehru.emailapi.config.email.SmtpServerConfig;
import com.rnehru.emailapi.exception.EmailNotSentException;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class SmtpClientTest {

    private SimpleSmtpServer server;

    @BeforeEach
    void setUp() throws IOException {
        server = SimpleSmtpServer.start(1065);
        server.reset();
    }

    @AfterEach
    void close() {
        server.close();
        server.stop();
    }

    @Test
    void sendEmail_sendsEmail() throws EmailNotSentException {
        SmtpServerConfig config = new SmtpServerConfig();
        config.smtpHost = "localhost";
        config.smtpPort = "1065";
        config.fromAddress = "noreply@gmail.com";
        SmtpClient client = new SmtpClient(config);
        EmailBody emailBodyValid = new EmailBodyBuilder()
                .withBody("<h1>Some HTML</h1><p>Some text</p>")
                .withSubject("subject")
                .withToRecipients(new ArrayList<String>() {{ add("foo@gmail.com"); }})
                .build();

        client.sendEmail(emailBodyValid);
        assertEquals(1, server.getReceivedEmails().size());
        SmtpMessage email = server.getReceivedEmails().get(0);
        assertEquals("noreply@gmail.com", email.getHeaderValue("From"));
        assertEquals("subject", email.getHeaderValue("Subject"));
        assertEquals("foo@gmail.com", email.getHeaderValue("To"));
        assertTrue(email.getBody().contains("<h1>Some HTML</h1><p>Some text</p>"));
    }

    @Test
    void sendEmail_throwsException_whenEmailGenerationError() {
        SmtpServerConfig config = new SmtpServerConfig();
        config.smtpHost = "localhost";
        config.smtpPort = "1065";
        config.fromAddress = "noreply@gmail.com";
        SmtpClient client = new SmtpClient(config);
        EmailBody emailBody = new EmailBodyBuilder()
                .withBody("<h1>Some HTML</h1><p>Some text</p>")
                .withSubject("subject")
                .build();
        assertThrows(EmailNotSentException.class, () -> client.sendEmail(emailBody));
    }

    @Test
    void sendEmail_throwsException_whenServerError() {
        server.close();
        server.stop();
        SmtpServerConfig config = new SmtpServerConfig();
        config.smtpHost = "localhost";
        config.smtpPort = "1065";
        config.fromAddress = "noreply@gmail.com";
        SmtpClient client = new SmtpClient(config);
        EmailBody emailBody = new EmailBodyBuilder()
                .withBody("<h1>Some HTML</h1><p>Some text</p>")
                .withSubject("subject")
                .withToRecipients(new ArrayList<String>() {{ add("foo@gmail.com"); }})
                .withCcRecipients(new ArrayList<String>() {{ add("bar@gmail.com"); }})
                .withBccRecipients(new ArrayList<String>() {{ add("baz@gmail.com"); }})
                .build();
        assertThrows(EmailNotSentException.class, () -> client.sendEmail(emailBody));
    }

}
