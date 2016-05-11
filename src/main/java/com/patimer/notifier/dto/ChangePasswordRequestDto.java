package com.patimer.notifier.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * This class represents the Data Transfer Object (DTO) of Thing in the system.
 * DTO is used to transfer objects between the UI/External API to the data model.
 */
public class ChangePasswordRequestDto
{
    @NotEmpty
    private String currentPassword;

    @NotEmpty
    private String newPassword;

    public ChangePasswordRequestDto(){} // default constructor required by Jackson.

    public ChangePasswordRequestDto(String currentPassword, String newPassword)
    {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    @JsonProperty("currentPassword")
    public String getCurrentPassword()
    {
        return currentPassword;
    }

    @JsonProperty("newPassword")
    public String getNewPassword()
    {
        return newPassword;
    }
}
