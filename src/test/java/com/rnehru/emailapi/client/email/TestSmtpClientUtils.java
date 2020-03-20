package com.rnehru.emailapi.client.email;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSmtpClientUtils {

    private class Utils extends SmtpClientUtils{
        @Override Properties getProperties() { return new Properties(); }
        @Override Logger getLogger() { return null; }
    }

    private Utils utils = new Utils();

    @Test
    void flattenAddresses_returnsStringWithEmails_whenArrayNotNullOrEmpty() throws AddressException {
        Address add1 = new InternetAddress("foo@gmail.com");
        Address add2 = new InternetAddress("bar@gmail.com");
        Address[] addresses = new Address[]{add1, add2};

        String flattened = utils.flattenAddresses(addresses);
        assertEquals(" foo@gmail.com bar@gmail.com ", flattened);
    }

    @Test
    void flattenAddresses_returnsEmptyString_whenArrayEmpty() {
        Address[] addresses = new Address[]{};

        String flattened = utils.flattenAddresses(addresses);
        assertEquals(" ", flattened);
    }

    @Test
    void flattenAddresses_returnsEmptyString_whenArrayNull() {
        String flattened = utils.flattenAddresses(null);
        assertEquals(" ", flattened);
    }
}
