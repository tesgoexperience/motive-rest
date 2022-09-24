package com.motive.rest.exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// When a client tries to interact with an entity in an inappropriate way. For example, try to approve an already approved friendship request
@ResponseStatus(value=HttpStatus.CONFLICT)
public class BadInteraction  extends RuntimeException  {
    static final Logger log = LoggerFactory.getLogger(BadInteraction.class);

    public BadInteraction(String msg) {
        super(msg);
        log.error("Entity was searched for but no record was found: " + msg);
    }
}
