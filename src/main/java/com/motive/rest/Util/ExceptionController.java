package com.motive.rest.Util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.motive.rest.exceptions.BadUserInput;

@ControllerAdvice
public class ExceptionController {

    /**
     * This method hands jakarta constraint validation failures for DTOs.
     * 
     * @param ex the exception thrown by the jakarta validator
     * @return a map of <Field, error message> e.g. <"username", "username cannot be
     *         null">
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    /**
     * This method will return an error if the client request contains illogical data. For example, a password that is two characters long
     * 
     * @param ex the exception thrown
     * @return the error message
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadUserInput.class)
    @ResponseBody
    public String dtoObject(BadUserInput ex) {
       return ex.getMessage();
    }

    /**
     * This method handles all other errors thrown that do not have a handler
     * already implemented
     * 
     * @param ex the exception thrown by the jakarta validator
     * @return
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String handleServerErrors(Exception ex) {
        System.err.println(ex);
        return "Sorry, something went wrong :( " + ex.getMessage();
    }
}
