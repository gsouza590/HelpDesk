package com.gabriel.helpdesk.exceptions.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.gabriel.helpdesk.exceptions.DataIntegrityViolationException;
import com.gabriel.helpdesk.exceptions.ObjectNotFoundExceptions;
import com.gabriel.helpdesk.exceptions.StandardError;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ResourceExceptionHandler {

	@ExceptionHandler(ObjectNotFoundExceptions.class)
	public ResponseEntity<StandardError> objectNotFoundExceptions(ObjectNotFoundExceptions ex,
			HttpServletRequest request) {

		StandardError error = new StandardError(System.currentTimeMillis(), HttpStatus.NOT_FOUND.value(),
				"Object Not Found", ex.getMessage(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<StandardError> dataIntegrityViolationException(DataIntegrityViolationException ex,
			HttpServletRequest request) {

		StandardError error = new StandardError(System.currentTimeMillis(), HttpStatus.BAD_REQUEST.value(),
				"Violação de Dados", ex.getMessage(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

	}

	 @ExceptionHandler(AuthenticationException.class)
	    public ResponseEntity<StandardError> authenticationException(AuthenticationException ex,
	            HttpServletRequest request) {

	        StandardError error = new StandardError(System.currentTimeMillis(), HttpStatus.UNAUTHORIZED.value(),
	                "Authentication Error", ex.getMessage(), request.getRequestURI());
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
	    }
}
