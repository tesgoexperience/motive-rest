package com.motive.rest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@ResponseStatus(value=HttpStatus.FORBIDDEN)
public class UnauthorizedRequest extends AuthenticationException {

    static final Logger log = LoggerFactory.getLogger(UnauthorizedRequest.class);

    public UnauthorizedRequest(String msg) {
        super(msg);
        log.error("Unauthorized request: " + msg);
    }

}
