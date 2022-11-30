package com.motive.rest.exceptions;

import org.springframework.security.core.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnauthorizedRequest extends AuthenticationException {

    static final Logger log = LoggerFactory.getLogger(UnauthorizedRequest.class);

    public UnauthorizedRequest(String msg) {
        super(msg);
        log.error("Unauthorized request: " + msg);
    }

}
