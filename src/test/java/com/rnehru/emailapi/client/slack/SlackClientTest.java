package com.rnehru.emailapi.client.slack;

import com.rnehru.emailapi.model.EmailBody;
import com.rnehru.emailapi.utils.EmailBodyBuilder;
import org.junit.jupiter.api.Test;
import com.rnehru.emailapi.config.slack.SlackConfig;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SlackClientTest {

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
    private static final SlackClient sc = new SlackClient(new SlackConfig());

    @Test
    void coagulateRecipients_returnsString_whenEmailBodyHasAddresses() {
        EmailBody eb = new EmailBodyBuilder()
                .withToRecipients(toRecipients)
                .withCcRecipients(ccRecipients)
                .withBccRecipients(bccRecipients)
                .build();

        String s = sc.coagulateRecipients(eb);
        assertEquals("pop@gmail.com bang@gmail.com " +
                "baz@gmail.com bat@gmail.com " +
                "foo@gmail.com bar@gmail.com ", s);
    }
}
