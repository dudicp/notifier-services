package com.patimer.notifier.service;

import org.apache.commons.lang.Validate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class PasswordService
{
    private static final int BCRYPT_SALT_ROUNDS = 10;

    public String passwordHash(String password)
    {
        Validate.notEmpty(password);
        return BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_SALT_ROUNDS));
    }

    public boolean checkPassword(String password, String hashed)
    {
        Validate.notEmpty(password);
        Validate.notEmpty(hashed);

        return BCrypt.checkpw(password, hashed);
    }

}
