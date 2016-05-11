package com.patimer.notifier.service.engine.retriever;

import com.patimer.notifier.model.ItemType;

public abstract class ApartmentSearchItemsRetriever<ApartmentItem> implements SearchItemsRetriever
{
    public ItemType getItemType()
    {
        return ItemType.Apartment;
    }
}
