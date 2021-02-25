package com.http.demo.exception;

import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * map a specific status code to an exception by creating a 
 * ControllerAdvice and wrap them to produce a vnd.error:
 * @author Admin
 *
 */
@ControllerAdvice
@RequestMapping(produces = "application/vnd.error")
public class BookControllerAdvice {

	@ResponseBody
    @ExceptionHandler(BookIsbnAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    VndErrors bookIsbnAlreadyExistsExceptionHandler(BookIsbnAlreadyExistException ex) {
		// Vnd errors - vendor errors 
        return new VndErrors("error", ex.getMessage());
    }
	
	@ResponseBody
    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    VndErrors bookNotFoundExceptionHandler(BookNotFoundException ex) {
		// Vnd errors - vendor errors 
        return new VndErrors("error", ex.getMessage());
    }
}
