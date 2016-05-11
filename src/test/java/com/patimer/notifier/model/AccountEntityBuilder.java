package com.patimer.notifier.model;

import java.util.UUID;

public class AccountEntityBuilder extends ManagedEntityBuilder<AccountEntityBuilder>
{
    private String mail = "user1@gmail.com";
    private String name = null;
    private String phone = null;
    private NotificationType notificationType = NotificationType.Email;
    private String activationCode = UUID.randomUUID().toString();
    private String unsubscribeCode = UUID.randomUUID().toString();
    private String password = "123456";
    private AccountState accountState = AccountState.Activated;

    public AccountEntityBuilder withMail(String mail)
    {
        this.mail = mail;
        return this;
    }

    public AccountEntityBuilder withName(String name)
    {
        this.name = name;
        return this;
    }

    public AccountEntityBuilder withPhone(String phone)
    {
        this.phone = phone;
        return this;
    }

    public AccountEntityBuilder withNotificationType(NotificationType notificationType)
    {
        this.notificationType = notificationType;
        return this;
    }

    public AccountEntityBuilder withPassword(String password)
    {
        this.password = password;
        return this;
    }

    public AccountEntityBuilder withActivationCode(String activationCode)
    {
        this.activationCode = activationCode;
        return this;
    }

    public AccountEntityBuilder withUnsubscribeCode(String unsubscribeCode)
    {
        this.unsubscribeCode = unsubscribeCode;
        return this;
    }

    public AccountEntityBuilder withAccountState(AccountState accountState)
    {
        this.accountState = accountState;
        return this;
    }

    public AccountEntity build()
    {
        AccountEntity res = new AccountEntity(id, createdOn, modifiedOn, mail, notificationType, name, phone, password);
        res.setActivationCode(activationCode);
        res.setUnsubscribeCode(unsubscribeCode);
        res.setAccountState(accountState);
        return res;
    }
}
