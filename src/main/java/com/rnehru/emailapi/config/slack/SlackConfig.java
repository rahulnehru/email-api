package com.rnehru.emailapi.config.slack;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlackConfig {

    @Value("${slack.webhook.channel}")
    public String slackChannel;

    @Value("${slack.webhook.emoji}")
    public String slackEmoji;

    @Value("${slack.webhook.url}")
    public String slackWebHookUrl;

    @Value("${slack.webhook.username}")
    public String slackUsername;

}
