package com.rnehru.emailapi.client.email;

import com.rnehru.emailapi.model.EmailBody;
import org.slf4j.Logger;
import com.rnehru.emailapi.exception.EmailNotSentException;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static javax.mail.Message.RecipientType.BCC;
import static javax.mail.Message.RecipientType.CC;
import static javax.mail.Message.RecipientType.TO;

abstract class SmtpClientUtils {

    abstract Properties getProperties();

    abstract Logger getLogger();

    static final String ENCODE_FILE_NAME = "mail.mime.encodefilename";
    static final String MAIL_SMTP_HOST = "mail.smtp.host";
    static final String MAIL_SMTP_PORT = "mail.smtp.port";


    void enrichMessage(Message message, String fromAddress, EmailBody emailBody) throws EmailNotSentException {
        try {
            message.setFrom(new InternetAddress(fromAddress));
            setRecipients(message, TO, emailBody.getToRecipients());
            setRecipients(message, CC, emailBody.getCcRecipients());
            setRecipients(message, BCC, emailBody.getBccRecipients());
            message.setSubject(emailBody.getSubject());
            message.setContent(buildContent(emailBody));
        } catch (MessagingException | EmailNotSentException e) {
            throw new EmailNotSentException(e);
        }
    }

    private void setRecipients(Message message, Message.RecipientType type, List<String> recipientList) throws EmailNotSentException, MessagingException {
        if(recipientList!=null && !recipientList.isEmpty()) {
            message.setRecipients(type, buildInternetAddresses(recipientList));
        }
    }

    private Address[] buildInternetAddresses(List<String> addressesStr) throws EmailNotSentException {
        try {
            List<Address> addresses = new ArrayList<>();
            for (String address : addressesStr) {
                addresses.add(new InternetAddress(address));
            }
            return addresses.toArray(new Address[]{});
        } catch (AddressException e) {
            getLogger().warn("Could not verify {} as a valid email address");
            throw new EmailNotSentException(e);
        }
    }

    private MimeMultipart buildContent(EmailBody body) throws MessagingException {
        MimeMultipart content = new MimeMultipart("related");
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(body.getEmailBody(), "text/html; charset=UTF-8");
        content.addBodyPart(bodyPart);
        return content;
    }

    String flattenAddresses(Address[] addresses) {
        StringBuilder sb = new StringBuilder(" ");
        if (addresses != null) {
            for (Address address : Arrays.stream(addresses).distinct().collect(Collectors.toList())) {
                sb.append(address.toString()).append(" ");
            }
        }
        return sb.toString();
    }

}
