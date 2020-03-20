package com.rnehru.emailapi.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter @Setter
public final class EmailBody {

    private List<String> toRecipients;
    private List<String> ccRecipients;
    private List<String> bccRecipients;

    @NotNull
    private String emailBody;

    @NotNull
    private String subject;

}
