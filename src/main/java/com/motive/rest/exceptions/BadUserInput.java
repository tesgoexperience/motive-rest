package com.motive.rest.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadUserInput extends IllegalArgumentException {
    static final Logger log = LoggerFactory.getLogger(IllogicalRequest.class);

    public BadUserInput(String msg) {
        super(msg);
        log.error("User input invalid: " + msg);
    }
}