package com.patimer.notifier.service.validation;

import com.patimer.notifier.dto.ChangePasswordRequestDto;
import com.patimer.notifier.dto.ChangePasswordRequestDtoBuilder;
import com.patimer.notifier.service.exception.ValidationException;
import com.patimer.notifier.service.validation.EntityValidator;
import org.junit.Test;

public class TestEntityValidator
{
    private EntityValidator validator = new EntityValidator();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ValidateObject
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testValidateWhenValid()
    {
        // given
        ChangePasswordRequestDto changePasswordRequestDto = new ChangePasswordRequestDtoBuilder().build();

        // when
        validator.validate(changePasswordRequestDto);

        // then - no exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateWhenNull()
    {
        // given

        // when
        validator.validate(null);

        // then - expacted exception
    }

    @Test(expected = ValidationException.class)
    public void testValidateWhenInvalid()
    {
        // given
        ChangePasswordRequestDto changePasswordRequestDto = new ChangePasswordRequestDtoBuilder().withCurrentPassword(null).build();

        // when
        validator.validate(changePasswordRequestDto);

        // then - expacted exception
    }
}
