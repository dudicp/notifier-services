package com.patimer.notifier.service.validation;

import com.patimer.notifier.service.exception.ValidationException;
import org.apache.commons.lang.Validate;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.HashSet;
import java.util.Set;

public abstract class ObjectValidator
{
    private Validator validator;

    public ObjectValidator()
    {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    public <T> void validate(T obj)
    {
        Validate.notNull(obj);
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(obj);

        if(!constraintViolations.isEmpty())
        {
            Set<String> constraintViolationsMessages = new HashSet<>();
            for(ConstraintViolation<T> constraintViolation : constraintViolations)
            {
                constraintViolationsMessages.add("Property '" + constraintViolation.getPropertyPath().toString() + "'" + ":" + constraintViolation.getMessage());
            }

            throw new ValidationException(
                "Validation failed for object of class: '" + obj.getClass() + "'.",
                constraintViolationsMessages
            );
        }
    }
}
