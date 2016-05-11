package com.patimer.notifier.dto;

public class ChangePasswordRequestDtoBuilder
{
    private String currentPassword = "123456";
    private String newPassword = "1234567";

    public ChangePasswordRequestDtoBuilder withCurrentPassword(String currentPassword)
    {
        this.currentPassword = currentPassword;
        return this;
    }

    public ChangePasswordRequestDtoBuilder withNewPassword(String newPassword)
    {
        this.newPassword = newPassword;
        return this;
    }

    public ChangePasswordRequestDto build()
    {
        return new ChangePasswordRequestDto(currentPassword, newPassword);
    }
}
