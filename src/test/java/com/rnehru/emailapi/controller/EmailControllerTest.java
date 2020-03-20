package com.rnehru.emailapi.controller;

import com.rnehru.emailapi.model.EmailBody;
import com.rnehru.emailapi.utils.EmailBodyBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import com.rnehru.emailapi.client.email.EmailClient;
import com.rnehru.emailapi.client.slack.SlackClient;
import com.rnehru.emailapi.config.email.ClientProvider;
import com.rnehru.emailapi.config.slack.SlackConfig;
import com.rnehru.emailapi.exception.EmailNotSentException;
import com.rnehru.emailapi.exception.SlackNotSentException;
import com.rnehru.emailapi.model.Response;
import com.rnehru.emailapi.validator.EmailAddressValidator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EmailControllerTest {

    private final static List<String> toRecipients = new ArrayList<String>() {{
        add("pop@gmail.com");
        add("bang@gmail.com");
    }};
    private final static List<String> ccRecipients = new ArrayList<String>() {{
        add("baz@gmail.com");
        add("bat@gmail.com");
    }};
    private final static List<String> bccRecipients = new ArrayList<String>() {{
        add("foo@gmail.com");
        add("bar@gmail.com");
    }};
    private final static String bodyText = "body";
    private final static String subject = "subject";
    private final static SlackConfig slackConfig = new SlackConfig();
    private final EmailAddressValidator trueValidator = new TrueEmailValidator();
    private final EmailAddressValidator falseValidator = new FalseEmailValidator();
    private static final SlackClient slackClient = new SlackClient(slackConfig);



    @Test
    void sendEmail_returns400_whenEmailAddressesNotValid() {
        SlackClient slackClient = new SlackClient(slackConfig);
        EmailController controller = new EmailController(new TestEmailClientProvider(), slackClient, falseValidator);
        EmailBody body = new EmailBody();
        ResponseEntity response = controller.sendEmail(body);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void sendEmail_returns500_whenSlackClientThrowsAnError() {
        SlackClient slackClient = new ErroringSlackClient(slackConfig);
        EmailController controller = new EmailController(new TestEmailClientProvider(), slackClient, trueValidator);
        controller.slackWebHookEnabled = true;
        EmailBody body = new EmailBodyBuilder()
                .withBccRecipients(ccRecipients)
                .build();
        ResponseEntity response = controller.sendEmail(body);
        assertEquals(500, response.getStatusCodeValue());
        Response responseBody = (Response) response.getBody();
        assertEquals(500, responseBody.getCode());
        assertEquals("Could not send slack notification", responseBody.getMessage());
    }

    @Test
    void sendEmail_returns500_whenEmailClientThrowsAnError() {
        SlackClient slackClient = new SlackClient(slackConfig);
        EmailController controller = new EmailController(new ErroringTestEmailClientProvider(), slackClient, trueValidator);
        controller.slackWebHookEnabled = true;
        EmailBody body = new EmailBodyBuilder()
                .withBccRecipients(ccRecipients)
                .build();
        ResponseEntity response = controller.sendEmail(body);
        assertEquals(500, response.getStatusCodeValue());
        Response responseBody = (Response) response.getBody();
        assertEquals(500, responseBody.getCode());
        assertEquals("Could not send email", responseBody.getMessage());
    }

    @Test
    void sendEmail_returns200_whenEmailAddressesAreValidAndEmailSent() {
        EmailController controller = new EmailController(new TestEmailClientProvider(), slackClient, trueValidator);
        EmailBody body = new EmailBodyBuilder()
                .withBccRecipients(bccRecipients)
                .withCcRecipients(ccRecipients)
                .withBody(bodyText)
                .withSubject(subject)
                .build();
        assertEquals(200, controller.sendEmail(body).getStatusCodeValue());
    }

    @Test
    void recipientsAreValid_returnsTrue_whenCcListIsNotEmpty() {
        EmailController controller = new EmailController(new TestEmailClientProvider(), slackClient, trueValidator);
        EmailBody body = new EmailBody();
        body.setCcRecipients(ccRecipients);
        assertTrue(controller.recipientsAreValid(body));
    }

    @Test
    void recipientsAreValid_returnsTrue_whenBccListIsNotEmpty() {
        EmailController controller = new EmailController(new TestEmailClientProvider(), slackClient, trueValidator);
        EmailBody body = new EmailBody();
        body.setBccRecipients(bccRecipients);
        assertTrue(controller.recipientsAreValid(body));
    }

    @Test
    void recipientsAreValid_returnsTrue_whenToListIsNotEmpty() {
        EmailController controller = new EmailController(new TestEmailClientProvider(), slackClient, trueValidator);
        EmailBody body = new EmailBody();
        body.setBccRecipients(bccRecipients);
        assertTrue(controller.recipientsAreValid(body));
    }

    @Test
    void recipientsAreValid_returnsTrue_whenToCcAndBccListIsNotEmpty() {
        EmailController controller = new EmailController(new TestEmailClientProvider(), slackClient, trueValidator);
        EmailBody body = new EmailBody();
        body.setToRecipients(toRecipients);
        body.setCcRecipients(ccRecipients);
        body.setBccRecipients(bccRecipients);
        assertTrue(controller.recipientsAreValid(body));
    }

    @Test
    void recipientsAreValid_returnsFalse_whenToCcAndBccListAreEmpty() {
        List<String> empty = new ArrayList<>();
        EmailController controller = new EmailController(new TestEmailClientProvider(), slackClient, trueValidator);
        EmailBody body = new EmailBody();
        body.setToRecipients(empty);
        body.setCcRecipients(empty);
        body.setBccRecipients(empty);
        assertFalse(controller.recipientsAreValid(body));
    }

    @Test
    void recipientsAreValid_returnsFalse_whenToCcAndBccAreNull() {
        EmailController controller = new EmailController(new TestEmailClientProvider(), slackClient, trueValidator);
        EmailBody body = new EmailBody();
        assertFalse(controller.recipientsAreValid(body));
    }


    class TestEmailClient implements EmailClient {
        @Override
        public void sendEmail(EmailBody emailBody) { }
    }

    private class ErrorTestEmailClient implements EmailClient {
        @Override
        public void sendEmail(EmailBody emailBody) throws EmailNotSentException {
            throw new EmailNotSentException(new RuntimeException());
        }
    }

    private class ErroringSlackClient extends SlackClient {
        public ErroringSlackClient(SlackConfig slackConfig) {
            super(slackConfig);
        }

        @Override
        public void sendSlackNotification(EmailBody body) throws SlackNotSentException {
            throw new SlackNotSentException("foo");
        }
    }

    private class TestEmailClientProvider implements ClientProvider {
        @Override
        public EmailClient getClient() {
            return new TestEmailClient();
        }
    }

    private class ErroringTestEmailClientProvider implements ClientProvider {
        @Override
        public EmailClient getClient() {
            return new ErrorTestEmailClient();
        }
    }

    private class TrueEmailValidator extends EmailAddressValidator {
        @Override
        public boolean validateEmailAddress(String emailAddress) {
            return true;
        }
    }

    private class FalseEmailValidator extends EmailAddressValidator {
        @Override
        public boolean validateEmailAddress(String emailAddress) {
            return false;
        }
    }
}
