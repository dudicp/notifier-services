package com.patimer.notifier.model;


import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Document(collection = "accounts")
public class AccountEntity extends ManagedEntity
{
    @NotEmpty
    @Email
    private String mail;

    private String name;

    private String phone;

    @NotNull
    private NotificationType notificationType;

    @NotEmpty
    private String activationCode;

    @NotEmpty
    private String unsubscribeCode;

    @NotEmpty
    private String password;

    @NotNull
    private AccountState accountState;

    protected AccountEntity(){} // default constructor required by mongodb

    public AccountEntity(
        String mail,
        NotificationType notificationType,
        String name,
        String phone,
        String password
    )
    {
        super();
        this.mail = mail;
        this.notificationType = notificationType;
        this.name = name;
        this.phone = phone;
        this.password = password;
    }

    public AccountEntity(
        UUID id,
        Date createdOn,
        Date modifiedOn,
        String mail,
        NotificationType notificationType,
        String name,
        String phone,
        String password
    )
    {
        super(id, createdOn, modifiedOn);
        this.mail = mail;
        this.notificationType = notificationType;
        this.name = name;
        this.phone = phone;
        this.password = password;
    }

    public AccountEntity(AccountEntity other)
    {
        super(other.getId(), other.getCreatedOn(), other.getModifiedOn());
        this.mail = other.getMail();
        this.notificationType = other.getNotificationType();
        this.name = other.getName();
        this.phone = other.getPhone();
        this.password = other.getPassword();
        this.activationCode = other.getActivationCode();
        this.unsubscribeCode = other.getUnsubscribeCode();
        this.accountState = other.getAccountState();
    }

    public String getMail()
    {
        return mail;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public NotificationType getNotificationType()
    {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType)
    {
        this.notificationType = notificationType;
    }

    public String getActivationCode()
    {
        return activationCode;
    }

    public void setActivationCode(String activationCode)
    {
        this.activationCode = activationCode;
    }

    public String getUnsubscribeCode()
    {
        return unsubscribeCode;
    }

    public void setUnsubscribeCode(String unsubscribeCode)
    {
        this.unsubscribeCode = unsubscribeCode;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public AccountState getAccountState()
    {
        return accountState;
    }

    public void setAccountState(AccountState accountState)
    {
        this.accountState = accountState;
    }
}
