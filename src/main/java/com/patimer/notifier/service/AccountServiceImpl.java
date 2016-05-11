package com.patimer.notifier.service;

import com.patimer.notifier.dao.AccountDao;
import com.patimer.notifier.dto.AccountDto;
import com.patimer.notifier.dto.ChangePasswordRequestDto;
import com.patimer.notifier.model.AccountEntity;
import com.patimer.notifier.model.AccountState;
import com.patimer.notifier.service.authentication.AuthenticationService;
import com.patimer.notifier.service.converter.AccountConverter;
import com.patimer.notifier.service.exception.NotFoundException;
import com.patimer.notifier.service.notification.NotificationService;
import com.patimer.notifier.service.validation.DtoValidator;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service(value = "accountService")
public class AccountServiceImpl implements AccountService
{
    private AccountDao accountDao;
    private AccountConverter accountConverter;
    private DtoValidator dtoValidator;
    private AuthenticationService authenticationService;
    private NotificationService notificationService;
    private PasswordService passwordService;


    @Autowired
    public AccountServiceImpl(
        AccountDao accountDao,
        AccountConverter accountConverter,
        DtoValidator dtoValidator,
        AuthenticationService authenticationService,
        NotificationService notificationService,
        PasswordService passwordService
    )
    {
        Validate.notNull(accountDao);
        Validate.notNull(accountConverter);
        Validate.notNull(dtoValidator);
        Validate.notNull(authenticationService);
        Validate.notNull(notificationService);
        Validate.notNull(passwordService);
        this.accountDao = accountDao;
        this.accountConverter = accountConverter;
        this.dtoValidator = dtoValidator;
        this.authenticationService = authenticationService;
        this.notificationService = notificationService;
        this.passwordService = passwordService;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Account CRUD
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public AccountDto create(AccountDto accountDto)
    {
        Validate.notNull(accountDto);
        dtoValidator.validateManagedObjectForCreate(accountDto);

        String mail = accountDto.getMail();
        AccountEntity existingAccount = accountDao.findByMail(mail);

        if(existingAccount != null)
        {
            if( existingAccount.getAccountState() != AccountState.Registered)
            {
                throw new IllegalStateException("Account already exists for mail address: '" + mail +"'.");
            }
            else // account is in registered state.
            {
                // should I generate a new activation code or reuse the exiting one?
                notificationService.sendActivationCode(existingAccount.getMail(), existingAccount.getActivationCode());
                return accountConverter.convertToDto(existingAccount);
            }
        }
        else // account with this mail doesn't exists.
        {
            // Create account entity and populate with generatred random values.
            AccountEntity accountEntity = accountConverter.convertForCreate(accountDto);
            accountEntity.setAccountState(AccountState.Registered);
            accountEntity.setActivationCode(UUID.randomUUID().toString());
            accountEntity.setUnsubscribeCode(UUID.randomUUID().toString());
            String hashedPassword = passwordService.passwordHash(accountEntity.getPassword());
            accountEntity.setPassword(hashedPassword);

            // store it.
            AccountEntity storedAccountEntity = accountDao.create(accountEntity);
            notificationService.sendActivationCode(accountEntity.getMail(), accountEntity.getActivationCode());

            return accountConverter.convertToDto(storedAccountEntity);
        }
    }

    @Override
    public AccountDto update(AccountDto accountDto) throws NotFoundException
    {
        Validate.notNull(accountDto);
        dtoValidator.validateManagedObjectForUpdate(accountDto);

        AccountEntity existingAccountEntity = accountDao.getById(accountDto.getId());
        AccountEntity mergedAccountEntity = accountConverter.mergeForUpdate(accountDto, existingAccountEntity);
        AccountEntity storedAccountEntity = accountDao.update(mergedAccountEntity);

        return accountConverter.convertToDto(storedAccountEntity);
    }

    @Override
    public void delete(UUID accountId) throws NotFoundException
    {
        Validate.notNull(accountId);

        accountDao.delete(accountId);
    }

    @Override
    public AccountDto getById(UUID accountId) throws NotFoundException
    {
        Validate.notNull(accountId);

        AccountEntity accountEntity = accountDao.getById(accountId);
        return accountConverter.convertToDto(accountEntity);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Account On Boardingand special operations
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void activate(UUID accountId, String activationCode) throws NotFoundException
    {
        Validate.notNull(accountId);
        Validate.notEmpty(activationCode);

        AccountEntity accountEntity = accountDao.getById(accountId);
        AccountState accountState = accountEntity.getAccountState();

        switch (accountState)
        {
            case Activated:
            {
                return; // nothing do be done as the account already activated.
            }
            case Disabled:
            {
                throw new IllegalStateException("Account disabled for mail address: '" + accountEntity.getMail() +"'.");
            }
            case Registered:
            {
                if(activationCode.equalsIgnoreCase(accountEntity.getActivationCode()))
                {
                    accountEntity.setAccountState(AccountState.Activated);
                    accountDao.update(accountEntity);
                }
                else
                {
                    throw new BadCredentialsException("bad activation code for account id: '" + accountId + "'.");
                }

                break;
            }
            default:
                throw new IllegalArgumentException("Unsupported account state: '" + accountState + "'.");
        }
    }

    @Override
    public void changePassword(UUID accountId, ChangePasswordRequestDto changePasswordRequest)
    {
        Validate.notNull(accountId);
        Validate.notNull(changePasswordRequest);
        dtoValidator.validate(changePasswordRequest);
        Validate.isTrue(!changePasswordRequest.getCurrentPassword().equals(changePasswordRequest.getNewPassword()));

        AccountEntity existingAccountEntity = accountDao.getById(accountId);

        // 1. Make sure the user can authenticate with the current password.
        authenticationService.authenticate(existingAccountEntity.getMail(), changePasswordRequest.getCurrentPassword());

        // 2. Change the password.
        String hashedPassword = passwordService.passwordHash(changePasswordRequest.getNewPassword());
        existingAccountEntity.setPassword(hashedPassword);
        accountDao.update(existingAccountEntity);
    }

    @Override
    public void forgotPassword(String emailAddress)
    {
        Validate.notEmpty(emailAddress);

        // TODO: implement
        throw new NotImplementedException();
    }

    @Override
    public void unsubscribe(UUID accountId, String unsubscribeCode)
    {
        Validate.notNull(accountId);
        Validate.notEmpty(unsubscribeCode);

        try
        {
            AccountEntity accountEntity = accountDao.getById(accountId);
            if(accountEntity.getAccountState() != AccountState.Disabled)
            {
                if(accountEntity.getUnsubscribeCode().equalsIgnoreCase(unsubscribeCode))
                {
                    accountEntity.setAccountState(AccountState.Disabled);
                    accountDao.update(accountEntity);
                }
                else
                {
                    throw new BadCredentialsException("bad unsubscribe code for account id: '" + accountId + "'.");
                }
            }
        }
        catch (NotFoundException e)
        {
            // ignore
        }
    }
}
