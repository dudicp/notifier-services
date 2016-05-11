package com.patimer.notifier.service.validation;

import com.patimer.notifier.dto.ManagedObjectDto;
import org.apache.commons.lang.Validate;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.stereotype.Component;

import javax.validation.*;
import java.util.Set;

@Component
public class DtoValidator extends ObjectValidator
{
    private Validator validator;

    public DtoValidator()
    {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    public <T extends ManagedObjectDto> void validateManagedObjectForCreate(T obj)
    {
        Validate.notNull(obj);
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(obj);

        if(!constraintViolations.isEmpty())
        {
            // allow only the id constraint to be violated.
            Validate.isTrue(constraintViolations.size() == 1);
            ConstraintViolation<T> constraintViolation = constraintViolations.iterator().next();
            Validate.isTrue(constraintViolation.getPropertyPath().equals(PathImpl.createPathFromString("id")));
        }
    }

    public <T extends ManagedObjectDto> void validateManagedObjectForUpdate(T obj)
    {
        Validate.notNull(obj);
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(obj);
        Validate.isTrue(constraintViolations.isEmpty());
    }
}
