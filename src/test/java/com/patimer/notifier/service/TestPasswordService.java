package com.patimer.notifier.service;

import org.junit.Test;
import org.springframework.util.Assert;

public class TestPasswordService
{
    private PasswordService passwordService = new PasswordService();

    @Test
    public void testPasswordHash()
    {
        // given

        // when
        String hashed = passwordService.passwordHash("123456");

        // then
        Assert.notNull(hashed);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPasswordHashWithNullPassword()
    {
        // given

        // when
        passwordService.passwordHash(null);

        // then - expacted exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPasswordHashWithEmptyPassword()
    {
        // given

        // when
        passwordService.passwordHash("");

        // then - expacted exception
    }

    @Test
    public void testCheckPasswordWhenMatched()
    {
        // given
        String password = "123456";
        String hashed = passwordService.passwordHash(password);

        // when
        boolean isMatched = passwordService.checkPassword(password, hashed);

        // then
        Assert.isTrue(isMatched);
    }


    @Test
    public void testCheckPasswordWhenNotMatched()
    {
        // given
        String password = "123456";
        String hashed = passwordService.passwordHash(password);

        // when
        boolean isMatched = passwordService.checkPassword("1234567", hashed);

        // then
        Assert.isTrue(!isMatched);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckPasswordWithNullPassword()
    {
        // given
        String password = "123456";
        String hashed = passwordService.passwordHash(password);

        // when
        passwordService.checkPassword(null, hashed);

        // then - expacted exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckPasswordWithEmptyPassword()
    {
        // given
        String password = "123456";
        String hashed = passwordService.passwordHash(password);

        // when
        passwordService.checkPassword("", hashed);

        // then - expacted exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckPasswordWithNullHashedPassword()
    {
        // given
        String password = "123456";

        // when
        passwordService.checkPassword(password, null);

        // then - expacted exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckPasswordWithEmptyHashedPassword()
    {
        // given
        String password = "123456";

        // when
        passwordService.checkPassword(password, "");

        // then - expacted exception
    }
}
