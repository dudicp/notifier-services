package com.patimer.notifier.service.exception;

import java.util.HashSet;
import java.util.Set;

public class ValidationException extends RuntimeException
{
    Set<String> constraintViolationsMessages = new HashSet<>();

    public ValidationException(String message)
    {
        super(message);
    }

    public ValidationException(String message, Set<String> constraintViolationsMessages)
    {
        super(message);
        this.constraintViolationsMessages = constraintViolationsMessages;
    }

    public ValidationException(Throwable t)
    {
        super(t);
    }

    public ValidationException(String message, Throwable t)
    {
        super(message, t);
    }

    @Override
    public String getMessage()
    {
        return super.getMessage() + "\n"  + getConstraintViolationsPretty();
    }

    public Set<String> getConstraintViolationsMessages()
    {
        return constraintViolationsMessages;
    }

    public String getConstraintViolationsPretty()
    {
        String result = "";

        if(constraintViolationsMessages != null)
        {
            for(String message : constraintViolationsMessages)
            {
                result = result + message + ";";
            }
        }

        return result;
    }
}
