package com.rnehru.emailapi.config.email;

import com.rnehru.emailapi.client.email.EmailClient;

public interface ClientProvider {

    EmailClient getClient();
}
