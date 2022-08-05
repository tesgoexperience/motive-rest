package com.motive.rest.exceptions;

import org.springframework.security.core.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnauthorizedAccessAttempt extends AuthenticationException {

    static final Logger log = LoggerFactory.getLogger(UnauthorizedAccessAttempt.class);

    public UnauthorizedAccessAttempt(String msg) {
        super(msg);
        log.error("Unauthorized access: " + msg);
    }

}