package com.patimer.notifier.service.validation;

import com.patimer.notifier.dto.AccountDto;
import com.patimer.notifier.dto.AccountDtoBuilder;
import com.patimer.notifier.service.validation.DtoValidator;
import org.junit.Test;

public class TestDtoValidator
{
    private DtoValidator dtoValidator = new DtoValidator();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ValidateManagedObjectForUpdate
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testValidateManagedObjectForUpdateWhenValid()
    {
        // given
        AccountDto accountDto = new AccountDtoBuilder().build();

        // when
        dtoValidator.validateManagedObjectForUpdate(accountDto);

        // then - no exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateManagedObjectForUpdateWhenNull()
    {
        // given

        // when
        dtoValidator.validateManagedObjectForUpdate(null);

        // then - expacted exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateManagedObjectForUpdateWhenInvalid()
    {
        // given
        AccountDto accountDto = new AccountDtoBuilder().withId(null).build();

        // when
        dtoValidator.validateManagedObjectForUpdate(accountDto);

        // then - expacted exception
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ValidateManagedObjectForCreate
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testValidateManagedObjectForCreateWhenValid()
    {
        // given
        AccountDto accountDto = new AccountDtoBuilder().build();

        // when
        dtoValidator.validateManagedObjectForCreate(accountDto);

        // then - no exception
    }

    @Test
    public void testValidateManagedObjectForCreateWithNoId()
    {
        // given
        AccountDto accountDto = new AccountDtoBuilder().withId(null).build();

        // when
        dtoValidator.validateManagedObjectForCreate(accountDto);

        // then - no exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateManagedObjectForCreateWhenNull()
    {
        // given

        // when
        dtoValidator.validateManagedObjectForCreate(null);

        // then - expacted exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateManagedObjectForCreateWhenInvalid()
    {
        // given
        AccountDto accountDto = new AccountDtoBuilder().withMail("").build();

        // when
        dtoValidator.validateManagedObjectForCreate(accountDto);

        // then - expacted exception
    }
}
