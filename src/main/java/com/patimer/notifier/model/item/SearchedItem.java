package com.patimer.notifier.model.item;

import com.patimer.notifier.model.ItemType;

public abstract class SearchedItem
{
    public abstract ItemType getItemType();

    public abstract String getUniqueIdentifier();

    public abstract int hashCode();

    public abstract boolean equals(Object o);
}
