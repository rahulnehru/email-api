package com.rnehru.emailapi.controller;

import com.rnehru.emailapi.model.EmailBody;
import com.rnehru.emailapi.validator.EmailAddressValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.rnehru.emailapi.client.email.EmailClient;
import com.rnehru.emailapi.client.slack.SlackClient;
import com.rnehru.emailapi.config.email.ClientProvider;
import com.rnehru.emailapi.exception.EmailNotSentException;
import com.rnehru.emailapi.exception.SlackNotSentException;
import com.rnehru.emailapi.model.Response;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/email")
public class EmailController {

    private EmailClient emailClient;
    private SlackClient slackClient;
    private EmailAddressValidator validator;

    @Value("${slack.webhook.enabled}")
    boolean slackWebHookEnabled;

    @Autowired
    public EmailController(ClientProvider clientProvider, SlackClient slackClient, EmailAddressValidator validator) {
        this.emailClient = clientProvider.getClient();
        this.slackClient = slackClient;
        this.validator = validator;
    }

    @PostMapping(path = "/send")
    public ResponseEntity sendEmail(@RequestBody EmailBody emailBody) {
        if (!recipientsAreValid(emailBody)) return ResponseEntity.badRequest().build();
        try {
            emailClient.sendEmail(emailBody);
            if (slackWebHookEnabled) {
                slackClient.sendSlackNotification(emailBody);
            }
            return ResponseEntity.ok().body(new Response("Email sent", 200));
        } catch (EmailNotSentException e) {
            return ResponseEntity.status(500).body(new Response("Could not send email", 500));
        } catch (SlackNotSentException e) {
            return ResponseEntity.status(500).body(new Response("Could not send slack notification", 500));
        }
    }

    boolean recipientsAreValid(EmailBody emailBody) {
        List<String> allEmails = new ArrayList<>();
        foldIntoIfNotNull(allEmails, emailBody.getToRecipients());
        foldIntoIfNotNull(allEmails, emailBody.getCcRecipients());
        foldIntoIfNotNull(allEmails, emailBody.getBccRecipients());
        boolean allAreValid = allEmails.stream().allMatch(s -> validator.validateEmailAddress(s));
        return !allEmails.isEmpty() && allAreValid;
    }

    private void foldIntoIfNotNull(List<String> acc, List<String> arg) {
        if (arg != null) {
            acc.addAll(arg);
        }
    }
}
