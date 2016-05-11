package com.patimer.notifier.service.notification;

import com.patimer.notifier.model.ItemType;
import com.patimer.notifier.model.item.SearchedItem;

import java.util.List;

public interface NotificationService
{
    void sendActivationCode(String email, String activationCode);

    void sendFoundNewItems(String email, ItemType itemsType, List<SearchedItem> foundItems);
}
