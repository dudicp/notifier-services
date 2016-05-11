package com.patimer.notifier.service;

import com.patimer.notifier.dao.AccountDao;
import com.patimer.notifier.dto.AccountDto;
import com.patimer.notifier.dto.AccountDtoBuilder;
import com.patimer.notifier.dto.ChangePasswordRequestDto;
import com.patimer.notifier.dto.ChangePasswordRequestDtoBuilder;
import com.patimer.notifier.model.AccountEntity;
import com.patimer.notifier.model.AccountEntityBuilder;
import com.patimer.notifier.model.AccountState;
import com.patimer.notifier.service.authentication.AuthenticationService;
import com.patimer.notifier.service.converter.AccountConverter;
import com.patimer.notifier.service.exception.NotFoundException;
import com.patimer.notifier.service.notification.NotificationService;
import com.patimer.notifier.service.validation.DtoValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.Assert;

import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TestAccountService
{
    private AccountService accountService;

    @Mock
    private AccountDao accountDaoMock;

    @Mock
    private AuthenticationService authenticationServiceMock;

    @Mock
    private NotificationService notificationServiceMock;


    @Before
    public void setUp()
    {
        AccountConverter accountConverter = new AccountConverter();
        DtoValidator dtoValidator = new DtoValidator();
        PasswordService passwordService = new PasswordService();

        accountService =
            new AccountServiceImpl(
                accountDaoMock,
                accountConverter,
                dtoValidator,
                authenticationServiceMock,
                notificationServiceMock,
                passwordService
            );
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Create
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testCreateWhenMailNotExists()
    {
        // given
        AccountDto accountDto = new AccountDtoBuilder().build();
        when(accountDaoMock.findByMail(accountDto.getMail())).thenReturn(null);
        AccountEntity dummyAccountEntity = new AccountEntityBuilder().build();

        ArgumentCaptor<AccountEntity> accountEntityArgumentCaptor = ArgumentCaptor.forClass(AccountEntity.class);
        when(accountDaoMock.create(accountEntityArgumentCaptor.capture())).thenReturn(dummyAccountEntity);

        // when
        AccountDto createdAccountDto = accountService.create(accountDto);

        // then
        Assert.notNull(createdAccountDto);

        AccountEntity storedAccountEntity = accountEntityArgumentCaptor.getValue();
        Assert.notNull(storedAccountEntity);
        Assert.isTrue(storedAccountEntity.getAccountState() == AccountState.Registered);
        Assert.notNull(storedAccountEntity.getActivationCode());
        Assert.notNull(storedAccountEntity.getUnsubscribeCode());

        verify(notificationServiceMock, times(1)).sendActivationCode(storedAccountEntity.getMail(), storedAccountEntity.getActivationCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNullAccountDto()
    {
        // given

        // when
        accountService.create(null);

        // then - expacted exception
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateWhenMailExistsInDisableState()
    {
        // given
        AccountDto accountDto = new AccountDtoBuilder().build();
        AccountEntity existsAccountEntity = new AccountEntityBuilder().withMail(accountDto.getMail()).withAccountState(AccountState.Disabled).build();
        when(accountDaoMock.findByMail(accountDto.getMail())).thenReturn(existsAccountEntity);

        // when
        accountService.create(accountDto);

        // then - expected exception
    }

    @Test
    public void testCreateWhenMailExistsInRegisteredState()
    {
        // given
        AccountDto accountDto = new AccountDtoBuilder().build();
        AccountEntity existsAccountEntity = new AccountEntityBuilder().withMail(accountDto.getMail()).withAccountState(AccountState.Registered).build();
        when(accountDaoMock.findByMail(accountDto.getMail())).thenReturn(existsAccountEntity);

        // when
        AccountDto createdAccountDto = accountService.create(accountDto);

        // then
        Assert.notNull(createdAccountDto);

        verify(notificationServiceMock, times(1)).sendActivationCode(existsAccountEntity.getMail(), existsAccountEntity.getActivationCode());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Update
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testUpdateWhenExists()
    {
        // given
        AccountDto accountDto = new AccountDtoBuilder().build();
        AccountEntity existsAccountEntity = new AccountEntityBuilder().withId(accountDto.getId()).withMail(accountDto.getMail()).build();
        when(accountDaoMock.getById(accountDto.getId())).thenReturn(existsAccountEntity);
        when(accountDaoMock.update(any(AccountEntity.class))).thenReturn(existsAccountEntity);

        // when
        AccountDto updatedAccountDto = accountService.update(accountDto);

        // then
        Assert.notNull(updatedAccountDto);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateWhenNotExists()
    {
        // given
        AccountDto accountDto = new AccountDtoBuilder().build();
        when(accountDaoMock.getById(accountDto.getId())).thenThrow(new NotFoundException("unit-test"));

        // when
        accountService.update(accountDto);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateWithNullAccountDto()
    {
        // given

        // when
        accountService.update(null);

        // then - expected exception
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Delete
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testDeleteWhenExists()
    {
        // given
        UUID accountId = UUID.randomUUID();

        // when
        accountService.delete(accountId);

        // then
    }

    @Test(expected = NotFoundException.class)
    public void testDeleteWhenNotExists()
    {
        // given
        UUID accountId = UUID.randomUUID();
        doThrow(new NotFoundException("unit-test")).when(accountDaoMock).delete(accountId);

        // when
        accountService.delete(accountId);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteWithNullId()
    {
        // given

        // when
        accountService.delete(null);

        // then - expected exception
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // GetById
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGetByIdWhenExists()
    {
        // given
        UUID accountId = UUID.randomUUID();
        AccountEntity foundAccountEntity = new AccountEntityBuilder().withId(accountId).build();
        when(accountDaoMock.getById(accountId)).thenReturn(foundAccountEntity);

        // when
        AccountDto accountDto = accountService.getById(accountId);

        // then
        Assert.notNull(accountDto);
    }

    @Test(expected = NotFoundException.class)
    public void testGetByIdWhenNotExists()
    {
        // given
        UUID accountId = UUID.randomUUID();
        when(accountDaoMock.getById(accountId)).thenThrow(new NotFoundException("unit-test"));

        // when
        accountService.getById(accountId);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetByIdWhenNullId()
    {
        // given

        // when
        accountService.getById(null);

        // then - expected exception
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Activate
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testActivateWhenAccountRegisteredAndActivationCodeMatched()
    {
        // given
        UUID accountId = UUID.randomUUID();
        String activationCode = UUID.randomUUID().toString();
        AccountEntity foundAccountEntity =
            new AccountEntityBuilder()
                .withId(accountId)
                .withActivationCode(activationCode)
                .withAccountState(AccountState.Registered)
                .build();

        when(accountDaoMock.getById(accountId)).thenReturn(foundAccountEntity);

        AccountEntity dummyAccountEntity = new AccountEntityBuilder().build();
        ArgumentCaptor<AccountEntity> accountEntityArgumentCaptor = ArgumentCaptor.forClass(AccountEntity.class);
        when(accountDaoMock.update(accountEntityArgumentCaptor.capture())).thenReturn(dummyAccountEntity);

        // when
        accountService.activate(accountId, activationCode);

        // then
        AccountEntity storedAccountEntity = accountEntityArgumentCaptor.getValue();
        Assert.notNull(storedAccountEntity);
        Assert.isTrue(storedAccountEntity.getAccountState() == AccountState.Activated);
    }

    @Test(expected = BadCredentialsException.class)
    public void testActivateWhenAccountRegisteredAndActivationCodeNotMatched()
    {
        // given
        UUID accountId = UUID.randomUUID();
        String activationCode = UUID.randomUUID().toString();
        AccountEntity foundAccountEntity =
            new AccountEntityBuilder()
                .withId(accountId)
                .withAccountState(AccountState.Registered)
                .build();

        when(accountDaoMock.getById(accountId)).thenReturn(foundAccountEntity);

        // when
        accountService.activate(accountId, activationCode);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActivateWithNullId()
    {
        // given
        String activationCode = UUID.randomUUID().toString();

        // when
        accountService.activate(null, activationCode);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActivateWithNullActivationCode()
    {
        // given
        UUID accountId = UUID.randomUUID();

        // when
        accountService.activate(accountId, null);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActivateWithEmptyActivationCode()
    {
        // given
        UUID accountId = UUID.randomUUID();

        // when
        accountService.activate(accountId, "");

        // then - expected exception
    }

    @Test(expected = NotFoundException.class)
    public void testActivateWhenNotExists()
    {
        // given
        UUID accountId = UUID.randomUUID();
        String activationCode = UUID.randomUUID().toString();

        when(accountDaoMock.getById(accountId)).thenThrow(new NotFoundException("unit-test"));

        // when
        accountService.activate(accountId, activationCode);

        // then - expected exception
    }

    @Test
    public void testActivateWhenAlreadyActivated()
    {
        // given
        UUID accountId = UUID.randomUUID();
        String activationCode = UUID.randomUUID().toString();
        AccountEntity foundAccountEntity =
            new AccountEntityBuilder()
                .withId(accountId)
                .withActivationCode(activationCode)
                .withAccountState(AccountState.Activated)
                .build();

        when(accountDaoMock.getById(accountId)).thenReturn(foundAccountEntity);

        // when
        accountService.activate(accountId, activationCode);

        // then
        verify(accountDaoMock, times(1)).getById(accountId);
        verifyNoMoreInteractions(accountDaoMock);
    }

    @Test(expected = IllegalStateException.class)
    public void testActivateWhenAccountDisabled()
    {
        // given
        UUID accountId = UUID.randomUUID();
        String activationCode = UUID.randomUUID().toString();
        AccountEntity foundAccountEntity =
            new AccountEntityBuilder()
                .withId(accountId)
                .withAccountState(AccountState.Disabled)
                .build();

        when(accountDaoMock.getById(accountId)).thenReturn(foundAccountEntity);

        // when
        accountService.activate(accountId, activationCode);

        // then - expected exception
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ChangePassword
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testChangePassword()
    {
        // given
        UUID accountId = UUID.randomUUID();
        String password = "1234";
        ChangePasswordRequestDto changePasswordRequestDto = new ChangePasswordRequestDtoBuilder().withCurrentPassword(password).build();
        AccountEntity foundAccountEntity = new AccountEntityBuilder().withId(accountId).withPassword(password).build();
        when(accountDaoMock.getById(accountId)).thenReturn(foundAccountEntity);
        when(authenticationServiceMock.authenticate(foundAccountEntity.getMail(), changePasswordRequestDto.getCurrentPassword())).thenReturn(new SessionPrincipalBuilder().build());

        AccountEntity dummyAccountEntity = new AccountEntityBuilder().build();
        ArgumentCaptor<AccountEntity> accountEntityArgumentCaptor = ArgumentCaptor.forClass(AccountEntity.class);
        when(accountDaoMock.update(accountEntityArgumentCaptor.capture())).thenReturn(dummyAccountEntity);

        // when
        accountService.changePassword(accountId, changePasswordRequestDto);

        // then
        AccountEntity storedAccountEntity = accountEntityArgumentCaptor.getValue();
        Assert.notNull(storedAccountEntity);
        Assert.isTrue(!storedAccountEntity.getPassword().equals(password));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangePasswordWithNullId()
    {
        // given
        String password = "1234";
        ChangePasswordRequestDto changePasswordRequestDto = new ChangePasswordRequestDtoBuilder().withCurrentPassword(password).build();

        // when
        accountService.changePassword(null, changePasswordRequestDto);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangePasswordWithNullRequest()
    {
        // given
        UUID accountId = UUID.randomUUID();

        // when
        accountService.changePassword(accountId, null);

        // then - expected exception
    }

    @Test(expected = NotFoundException.class)
    public void testChangePasswordWhenAccountNotExists()
    {
        // given
        UUID accountId = UUID.randomUUID();
        String password = "1234";
        ChangePasswordRequestDto changePasswordRequestDto = new ChangePasswordRequestDtoBuilder().withCurrentPassword(password).build();
        when(accountDaoMock.getById(accountId)).thenThrow(new NotFoundException("unit-test"));

        // when
        accountService.changePassword(accountId, changePasswordRequestDto);

        // then - expected exception
    }

    @Test(expected = BadCredentialsException.class)
    public void testChangePasswordWhenBadCredentials()
    {
        // given
        UUID accountId = UUID.randomUUID();
        String password = "1234";
        ChangePasswordRequestDto changePasswordRequestDto = new ChangePasswordRequestDtoBuilder().withCurrentPassword("12345").build();
        AccountEntity foundAccountEntity = new AccountEntityBuilder().withId(accountId).withPassword(password).build();
        when(accountDaoMock.getById(accountId)).thenReturn(foundAccountEntity);
        when(authenticationServiceMock.authenticate(foundAccountEntity.getMail(), changePasswordRequestDto.getCurrentPassword())).thenThrow(new BadCredentialsException("unit-test"));

        // when
        accountService.changePassword(accountId, changePasswordRequestDto);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangePasswordWhenSameCredentials()
    {
        // given
        UUID accountId = UUID.randomUUID();
        String password = "1234";
        ChangePasswordRequestDto changePasswordRequestDto = new ChangePasswordRequestDtoBuilder().withCurrentPassword(password).withNewPassword(password).build();

        // when
        accountService.changePassword(accountId, changePasswordRequestDto);

        // then - expected exception
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ForgotPassword
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // testForgotPassword
    // testForgotPasswordWithNullEmail
    // testForgotPasswordWithEmptyEmail

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Unsubscribe
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testUnsubscribeWhenAccountRegisteredAndUnscubscribeCodeMatched()
    {
        // given
        UUID accountId = UUID.randomUUID();
        String unsubscribeCode = UUID.randomUUID().toString();
        AccountEntity foundAccountEntity =
            new AccountEntityBuilder()
                .withId(accountId)
                .withUnsubscribeCode(unsubscribeCode)
                .withAccountState(AccountState.Activated)
                .build();

        when(accountDaoMock.getById(accountId)).thenReturn(foundAccountEntity);

        AccountEntity dummyAccountEntity = new AccountEntityBuilder().build();
        ArgumentCaptor<AccountEntity> accountEntityArgumentCaptor = ArgumentCaptor.forClass(AccountEntity.class);
        when(accountDaoMock.update(accountEntityArgumentCaptor.capture())).thenReturn(dummyAccountEntity);

        // when
        accountService.unsubscribe(accountId, unsubscribeCode);

        // then
        AccountEntity storedAccountEntity = accountEntityArgumentCaptor.getValue();
        Assert.notNull(storedAccountEntity);
        Assert.isTrue(storedAccountEntity.getAccountState() == AccountState.Disabled);
    }

    @Test(expected = BadCredentialsException.class)
    public void testUnsubscribeWhenAccountRegisteredAndUnscubscribeCodeNotMatched()
    {
        // given
        UUID accountId = UUID.randomUUID();
        String unsubscribeCode = UUID.randomUUID().toString();
        AccountEntity foundAccountEntity =
            new AccountEntityBuilder()
                .withId(accountId)
                .withAccountState(AccountState.Activated)
                .build();

        when(accountDaoMock.getById(accountId)).thenReturn(foundAccountEntity);

        // when
        accountService.unsubscribe(accountId, unsubscribeCode);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnsubscribeWithNullId()
    {
        // given
        String unsubscribeCode = UUID.randomUUID().toString();

        // when
        accountService.unsubscribe(null, unsubscribeCode);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnsubscribeWithNullCode()
    {
        // given
        UUID accountId = UUID.randomUUID();

        // when
        accountService.unsubscribe(accountId, null);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnsubscribeWithEmptyCode()
    {
        // given
        UUID accountId = UUID.randomUUID();

        // when
        accountService.unsubscribe(accountId, "");

        // then - expected exception
    }

    @Test
    public void testUnsubscribeWhenNotExists()
    {
        // given
        UUID accountId = UUID.randomUUID();
        String unsubscribeCode = UUID.randomUUID().toString();
        when(accountDaoMock.getById(accountId)).thenThrow(new NotFoundException("unit-test"));

        // when
        accountService.unsubscribe(accountId, unsubscribeCode);

        // then
        verify(accountDaoMock, times(1)).getById(accountId);
        verifyNoMoreInteractions(accountDaoMock);
    }

    @Test
    public void testUnsubscribeWhenAlreadyDisabled()
    {
        // given
        UUID accountId = UUID.randomUUID();
        String unsubscribeCode = UUID.randomUUID().toString();
        AccountEntity foundAccountEntity =
            new AccountEntityBuilder()
                .withId(accountId)
                .withUnsubscribeCode(unsubscribeCode)
                .withAccountState(AccountState.Disabled)
                .build();

        when(accountDaoMock.getById(accountId)).thenReturn(foundAccountEntity);

        // when
        accountService.unsubscribe(accountId, unsubscribeCode);

        // then
        verify(accountDaoMock, times(1)).getById(accountId);
        verifyNoMoreInteractions(accountDaoMock);
    }
}
