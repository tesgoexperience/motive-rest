package com.motive.rest.exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class MissingParameterException extends Exception {

    static final Logger log = LoggerFactory.getLogger(MissingParameterException.class);

    public MissingParameterException(String msg) {
        super(msg);
        log.error("Missing parameter: " + msg);
    }

}