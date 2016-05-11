package com.patimer.notifier.service.authentication;

import com.patimer.notifier.dao.AccountDao;
import com.patimer.notifier.model.AccountEntity;
import com.patimer.notifier.model.AccountEntityBuilder;
import com.patimer.notifier.model.AccountState;
import com.patimer.notifier.service.PasswordService;
import com.patimer.notifier.service.authentication.AuthenticationService;
import com.patimer.notifier.service.authentication.AuthenticationServiceImpl;
import com.patimer.notifier.service.authentication.SessionPrincipal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.Assert;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestAuthenticationService
{
    private AuthenticationService authenticationService;
    private PasswordService passwordService = new PasswordService();

    @Mock
    private AccountDao accountDaoMock;

    @Before
    public void setUp()
    {
        authenticationService = new AuthenticationServiceImpl(accountDaoMock, passwordService);
    }

    @Test
    public void testSuccessfulAuthentication()
    {
        // given
        String mail = "test@test.com";
        String password = "1234";
        String hashed = passwordService.passwordHash(password);
        AccountEntity foundAccountEntity = new AccountEntityBuilder().withMail(mail).withPassword(hashed).build();
        when(accountDaoMock.findByMail(mail)).thenReturn(foundAccountEntity);

        // when
        SessionPrincipal sessionPrincipal = authenticationService.authenticate(mail, password);

        // then
        Assert.notNull(sessionPrincipal);
        Assert.isTrue(sessionPrincipal.getAccountId().equals(foundAccountEntity.getId()));
        Assert.isTrue(sessionPrincipal.getEmail().equals(mail));
        Assert.isTrue(!sessionPrincipal.isAdmin());
    }

    @Test(expected = BadCredentialsException.class)
    public void testFailedAuthentication()
    {
        // given
        String mail = "test@test.com";
        String password = "1234";
        String hashed = passwordService.passwordHash(password);
        AccountEntity foundAccountEntity = new AccountEntityBuilder().withMail(mail).withPassword(hashed).build();
        when(accountDaoMock.findByMail(mail)).thenReturn(foundAccountEntity);

        // when
        authenticationService.authenticate(mail, "12345");

        // then - expected exception
    }

    @Test(expected = BadCredentialsException.class)
    public void testAuthenticationWhenAccountDisabled()
    {
        // given
        String mail = "test@test.com";
        String password = "1234";
        String hashed = passwordService.passwordHash(password);
        AccountEntity foundAccountEntity = new AccountEntityBuilder().withMail(mail).withPassword(hashed).withAccountState(AccountState.Disabled).build();
        when(accountDaoMock.findByMail(mail)).thenReturn(foundAccountEntity);

        // when
        authenticationService.authenticate(mail, password);

        // then - expected exception
    }

    @Test(expected = BadCredentialsException.class)
    public void testAuthenticationWhenAccountNotFound()
    {
        // given
        String mail = "test@test.com";
        String password = "1234";
        when(accountDaoMock.findByMail(mail)).thenReturn(null);

        // when
        authenticationService.authenticate(mail, password);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAuthenticationWithNullName()
    {
        // given
        String password = "1234";

        // when
        authenticationService.authenticate(null, password);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAuthenticationWithEmptyName()
    {
        // given
        String password = "1234";

        // when
        authenticationService.authenticate("", password);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAuthenticationWithNullPassword()
    {
        // given
        String mail = "user@test.com";

        // when
        authenticationService.authenticate(mail, null);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAuthenticationWithEmptyPassword()
    {
        // given
        String mail = "user@test.com";

        // when
        authenticationService.authenticate(mail, "");

        // then - expected exception
    }
}
