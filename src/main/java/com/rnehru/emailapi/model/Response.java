package com.rnehru.emailapi.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public final class Response {

    private String message;
    private int code;

    public Response(String message, int code) {
        this.message = message;
        this.code = code;
    }
}
