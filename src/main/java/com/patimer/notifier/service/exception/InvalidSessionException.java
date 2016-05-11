package com.patimer.notifier.service.exception;

public class InvalidSessionException extends Exception
{
    public InvalidSessionException(String message)
    {
        super(message);
    }

    public InvalidSessionException(Throwable t)
    {
        super(t);
    }

    public InvalidSessionException(String message, Throwable t)
    {
        super(message, t);
    }
}
