package com.patimer.notifier.dto;


public class AccountDtoBuilder extends ManagedObjectBuilder<AccountDtoBuilder>
{
    private String mail = "user1@gmail.com";
    private String name = "user1";
    private String phone = null;
    private NotificationTypeDto notificationType = NotificationTypeDto.Email;
    private String password = "123456";

    public AccountDtoBuilder withMail(String mail)
    {
        this.mail = mail;
        return this;
    }

    public AccountDtoBuilder withName(String name)
    {
        this.name = name;
        return this;
    }

    public AccountDtoBuilder withPhone(String phone)
    {
        this.phone = phone;
        return this;
    }

    public AccountDtoBuilder withNotificationType(NotificationTypeDto notificationType)
    {
        this.notificationType = notificationType;
        return this;
    }

    public AccountDtoBuilder withPassword(String password)
    {
        this.password = password;
        return this;
    }

    public AccountDto build()
    {
        return new AccountDto(id, createdOn, modifiedOn, mail, notificationType, password, phone, name);
    }
}
