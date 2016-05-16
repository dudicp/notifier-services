package com.patimer.notifier.service.notification;

import com.patimer.notifier.model.AccountEntity;
import com.patimer.notifier.model.ItemType;
import com.patimer.notifier.model.item.SearchedItem;

import java.util.List;

public interface NotificationService
{
    void sendActivationCode(AccountEntity accountEntity, String activationCode);

    void sendFoundNewItems(AccountEntity accountEntity, ItemType itemsType, List<SearchedItem> foundItems);
}
