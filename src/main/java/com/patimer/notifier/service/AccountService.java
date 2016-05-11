package com.patimer.notifier.service;

import com.patimer.notifier.dto.AccountDto;
import com.patimer.notifier.dto.ChangePasswordRequestDto;
import com.patimer.notifier.service.exception.NotFoundException;

import java.util.UUID;

public interface AccountService
{
    AccountDto create(AccountDto accountDto);

    AccountDto update(AccountDto accountDto) throws NotFoundException;

    void delete(UUID accountId) throws NotFoundException;

    AccountDto getById(UUID accountId) throws NotFoundException;

    void activate(UUID accountId, String activationCode) throws NotFoundException;

    void changePassword(UUID accountId, ChangePasswordRequestDto changePasswordRequest);

    void forgotPassword(String emailAddress) throws NotFoundException;

    void unsubscribe(UUID accountId, String unsubscribeCode);

}
