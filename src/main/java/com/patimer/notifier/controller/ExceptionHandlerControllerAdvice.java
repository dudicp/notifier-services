package com.patimer.notifier.controller;

import com.patimer.notifier.service.exception.InvalidSessionException;
import com.patimer.notifier.service.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This is the global exception handler for the controllers layer.
 * Each exception which should be mapped to HttpStatus should be mapped in this class.
 */
@ControllerAdvice
public class ExceptionHandlerControllerAdvice
{
    @ExceptionHandler
    public void handleIllegalArgumentException(HttpServletResponse response, IllegalArgumentException e) throws IOException
    {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler
    public void handleInvalidSessionException(HttpServletResponse response, InvalidSessionException e) throws IOException
    {
        response.sendError(HttpStatus.UNAUTHORIZED.value());
    }

    @ExceptionHandler
    public void handleBadCredentialsException(HttpServletResponse response, BadCredentialsException e) throws IOException
    {
        response.sendError(HttpStatus.UNAUTHORIZED.value());
    }

    @ExceptionHandler
    public void handleNotFoundException(HttpServletResponse response, NotFoundException e) throws IOException
    {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }
}
