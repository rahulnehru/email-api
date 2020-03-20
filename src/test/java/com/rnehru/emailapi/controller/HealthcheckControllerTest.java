package com.rnehru.emailapi.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HealthcheckControllerTest {

    @Test
    void healthcheck_returns200() {
        assertEquals(200, new HealthCheckController().healthcheck().getStatusCodeValue());
    }

}
