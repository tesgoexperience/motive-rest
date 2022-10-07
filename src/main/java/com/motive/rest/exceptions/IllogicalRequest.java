package com.motive.rest.exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// When a client tries to interact with an entity in an illogical way. For example, try to approve an already approved friendship request
@ResponseStatus(value=HttpStatus.CONFLICT)
public class IllogicalRequest  extends RuntimeException  {
    static final Logger log = LoggerFactory.getLogger(IllogicalRequest.class);

    public IllogicalRequest(String msg) {
        super(msg);
        log.error("The request is not logical: " + msg);
    }
}