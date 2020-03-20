package com.rnehru.emailapi.client.email;

import com.rnehru.emailapi.exception.EmailNotSentException;
import com.rnehru.emailapi.model.EmailBody;

public interface EmailClient {

    void sendEmail(EmailBody emailBody) throws EmailNotSentException;

}
