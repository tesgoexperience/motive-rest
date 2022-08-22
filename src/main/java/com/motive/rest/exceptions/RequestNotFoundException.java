package com.motive.rest.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestNotFoundException  extends Exception  {

    static final Logger log = LoggerFactory.getLogger(RequestNotFoundException.class);

    public RequestNotFoundException(String msg) {
        super(msg);
        log.error("Follow request not found: " + msg);
    }
}
