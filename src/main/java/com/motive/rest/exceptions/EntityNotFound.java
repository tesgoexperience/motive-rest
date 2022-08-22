package com.motive.rest.exceptions;
import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND)
public class EntityNotFound extends EntityNotFoundException  {

    static final Logger log = LoggerFactory.getLogger(EntityNotFound.class);

    public EntityNotFound(String msg) {
        super(msg);
        log.error("Entity was searched for but no record was found: " + msg);
    }
}
