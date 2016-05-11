package com.patimer.notifier.dao;

import com.patimer.notifier.model.AccountEntity;
import com.patimer.notifier.service.exception.NotFoundException;

import java.util.UUID;

public interface AccountDao
{
    AccountEntity create(AccountEntity accountEntity);

    AccountEntity update(AccountEntity mergedAccountEntity);

    void delete(UUID accountId) throws NotFoundException;

    AccountEntity getById(UUID id) throws NotFoundException;

    AccountEntity findByMail(String mail);
}
