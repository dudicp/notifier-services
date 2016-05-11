package com.patimer.notifier.service.converter;

import com.patimer.notifier.dto.AccountDto;
import com.patimer.notifier.dto.NotificationTypeDto;
import com.patimer.notifier.model.AccountEntity;
import org.apache.commons.lang.Validate;
import org.springframework.stereotype.Component;


@Component
public class AccountConverter
{
    public AccountEntity convertForCreate(AccountDto accountDto)
    {
        AccountEntity entity = null;

        if(accountDto != null)
        {
            entity = new AccountEntity(
                accountDto.getMail(),
                accountDto.getNotificationType().toNotificationType(),
                accountDto.getName(),
                accountDto.getPhone(),
                accountDto.getPassword()
            );
        }

        return entity;
    }

    public AccountEntity mergeForUpdate(AccountDto accountDto, AccountEntity currentAccountEntity)
    {
        Validate.notNull(accountDto);
        Validate.notNull(currentAccountEntity);

        AccountEntity mergedEntity = new AccountEntity(currentAccountEntity);
        mergedEntity.setName(accountDto.getName());
        mergedEntity.setNotificationType(accountDto.getNotificationType().toNotificationType());
        mergedEntity.setPhone(accountDto.getPhone());
        // mail, password and the rest of the attributes doesn't updated from the converter flow.

        return mergedEntity;
    }

    public AccountDto convertToDto(AccountEntity accountEntity)
    {
        AccountDto dto = null;

        if (accountEntity != null)
        {
            dto = new AccountDto(
                accountEntity.getId(),
                accountEntity.getCreatedOn(),
                accountEntity.getModifiedOn(),
                accountEntity.getMail(),
                NotificationTypeDto.fromNotificationType(accountEntity.getNotificationType()),
                null, /*password should not leave the backend*/
                accountEntity.getPhone(),
                accountEntity.getName()
            );
        }

        return dto;
    }
}
