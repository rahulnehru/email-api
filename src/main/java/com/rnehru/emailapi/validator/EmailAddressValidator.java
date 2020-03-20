package com.rnehru.emailapi.validator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.List;

@Component
public class EmailAddressValidator {

    @Value("${email.domains}")
    private List<String> validDomains;

    public boolean validateEmailAddress(String emailAddress) {
        try {
            InternetAddress address = new InternetAddress(emailAddress);
            address.validate();
            String domain = emailAddress.split("@")[1];
            return validDomains.stream().anyMatch(p -> p.equals(domain));
        } catch (AddressException e) {
            return false;
        }
    }

}
