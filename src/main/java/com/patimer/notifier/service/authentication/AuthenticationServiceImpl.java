package com.patimer.notifier.service.authentication;

import com.patimer.notifier.dao.AccountDao;
import com.patimer.notifier.model.AccountEntity;
import com.patimer.notifier.model.AccountState;
import com.patimer.notifier.service.PasswordService;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationServiceImpl implements AuthenticationService
{
    private AccountDao accountDao;
    private PasswordService passwordService;

    @Autowired
    public AuthenticationServiceImpl(AccountDao accountDao, PasswordService passwordService)
    {
        Validate.notNull(accountDao);
        Validate.notNull(passwordService);
        this.accountDao = accountDao;
        this.passwordService = passwordService;
    }

    @Override
    public SessionPrincipal authenticate(String name, String password) throws BadCredentialsException
    {
        Validate.notEmpty(name);
        Validate.notEmpty(password);

        AccountEntity foundAccountEntity = accountDao.findByMail(name);

        if(foundAccountEntity != null && foundAccountEntity.getAccountState() != AccountState.Disabled)
        {
            boolean authenticate = passwordService.checkPassword(password, foundAccountEntity.getPassword());
            if(authenticate)
            {
                return new SessionPrincipal(foundAccountEntity.getId(), foundAccountEntity.getMail(), false /*isAdmin*/);
            }
        }

        throw new BadCredentialsException("Invalid username or password.");
    }
}
