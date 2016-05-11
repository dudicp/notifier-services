package com.patimer.notifier.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

/**
 * This class represents the Data Transfer Object (DTO) of Thing in the system.
 * DTO is used to transfer objects between the UI/External API to the data model.
 */
//@XmlRootElement(name = "account")
//@XmlType(propOrder={"id", "createdOn", "modifiedOn", "mail", "name", "phone", "notificationType", "password"})
public class AccountDto extends ManagedObjectDto
{
    @NotEmpty
    @Email
    private String mail;

    private String name;

    private String phone;

    @NotNull
    private NotificationTypeDto notificationType;

    @NotEmpty
    private String password;

    public AccountDto(){} // default constructor needed for create operations.

    public AccountDto(
        UUID id,
        String mail,
        NotificationTypeDto notificationType,
        String password,
        String phone,
        String name
    )
    {
        super(id);
        this.mail = mail;
        this.notificationType = notificationType;
        this.password = password;
        this.phone = phone;
        this.name = name;
    }

    public AccountDto(
        UUID id,
        Date createdOn,
        Date modifiedOn,
        String mail,
        NotificationTypeDto notificationType,
        String password,
        String phone,
        String name
    )
    {
        super(id, createdOn, modifiedOn);
        this.mail = mail;
        this.notificationType = notificationType;
        this.password = password;
        this.phone = phone;
        this.name = name;
    }

    @JsonProperty("mail")
    public String getMail()
    {
        return mail;
    }

    @JsonProperty("name")
    public String getName()
    {
        return name;
    }

    @JsonProperty("phone")
    public String getPhone()
    {
        return phone;
    }

    @JsonProperty("notificationType")
    public NotificationTypeDto getNotificationType()
    {
        return notificationType;
    }

    @JsonProperty("password")
    public String getPassword()
    {
        return password;
    }
}
