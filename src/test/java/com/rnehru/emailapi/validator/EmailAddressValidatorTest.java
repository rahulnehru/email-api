package com.rnehru.emailapi.validator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(properties = "email.domains:whitelisted.co.uk, gmail.com")
public class EmailAddressValidatorTest {

    @Autowired
    private EmailAddressValidator validator;

    @Test
    void validateEmailAddress_returnsTrue_whenEmailAddressIsValidAndDomainWhitelisted() {
        String email = "noreply@whitelisted.co.uk";
        assertTrue(validator.validateEmailAddress(email));
    }

    @Test
    void validateEmailAddress_returnsFalse_whenEmailAddressIsInvalid() {
        String email = "foofoo";
        assertFalse(validator.validateEmailAddress(email));
    }

    @Test
    void validateEmailAddress_returnsFalse_whenEmailAddressIsValidButNotWhitelisted() {
        String email = "noreply@yahoo.co.uk";
        assertFalse(validator.validateEmailAddress(email));
    }
}
