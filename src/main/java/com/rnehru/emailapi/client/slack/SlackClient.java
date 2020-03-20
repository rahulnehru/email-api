package com.rnehru.emailapi.client.slack;

import com.rnehru.emailapi.config.slack.SlackConfig;
import com.rnehru.emailapi.exception.SlackNotSentException;
import com.rnehru.emailapi.model.EmailBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class SlackClient {

    private WebClient client = WebClient.create();
    private static final Logger LOGGER = LoggerFactory.getLogger(SlackClient.class);


    private final SlackConfig slackConfig;

    @Autowired
    public SlackClient(SlackConfig slackConfig) {
        this.slackConfig = slackConfig;
    }

    public void sendSlackNotification(EmailBody body) throws SlackNotSentException {

        WebHookMessage message = new WebHookMessage();
        message.username = slackConfig.slackUsername;
        message.channelName = "#" + slackConfig.slackChannel;
        message.iconEmoji = ":" + slackConfig.slackEmoji + ":";
        message.text = "Email with subject [" + body.getSubject() + "] sent to " + coagulateRecipients(body);
        ResponseEntity response = client.post().uri(slackConfig.slackWebHookUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(message.toString())).retrieve().toBodilessEntity().block();
        if (response.getStatusCode().isError()) {
            LOGGER.error("Could not post to Slack");
            throw new SlackNotSentException("Could not post to Slack");
        } else {
            LOGGER.info("Posted message to Slack");
        }
    }

    String coagulateRecipients(EmailBody emailBody) {
        StringBuilder sb = new StringBuilder();
        for (String emailAddress : emailBody.getToRecipients()) {
            sb.append(emailAddress).append(" ");
        }
        for (String emailAddress : emailBody.getCcRecipients()) {
            sb.append(emailAddress).append(" ");
        }
        for (String emailAddress : emailBody.getBccRecipients()) {
            sb.append(emailAddress).append(" ");
        }
        return sb.toString();
    }

    class WebHookMessage {

        private String text;
        private String channelName;
        private String iconEmoji;
        private String username;

        @Override
        public String toString() {
            return "{\"username\":\"" + username + "\", \"text\": \"" + text + "\", \"channel_name\": \"" + channelName + "\", \"icon_emoji\":\"" + iconEmoji + "\"}";
        }
    }
}
